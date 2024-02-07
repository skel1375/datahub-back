package com.knusolution.datahub.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.IOUtils
import com.knusolution.datahub.domain.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.NoSuchElementException

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val userRepository: UserRepository,
    private val noticeFileRepository: NoticeFileRepository,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    private val amazonS3: AmazonS3,
) {
    val pageSize = 10

    //공지사항 불러오기 (페이지)
    fun getNotice(page:Int): Page<NoticeInfoDto> {
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("noticeId").descending())
        return noticeRepository.findAll(pageable).map{it.asInfoDto()}
    }

    //자세한 내용 불러오기, 모달 띄울 때 정보 요청
    @Transactional(readOnly = true)
    fun getNoticeData(noticeId: Long): NoticeContentResponse? {
        val notice = findNoticeById(noticeId)
        val noticeFiles= noticeFileRepository.findByNotice(notice)?.map{it.asInfoDto()}
        return NoticeContentResponse(notice.noticeTitle,notice.noticeContent,noticeFiles)
    }

    //작성한 공지사항 DB 저장
    @Transactional
    fun saveNotice(loginId: String, noticeTitle: String, noticeContent: String,files : List<MultipartFile>?) {
        val user = findUserByLoginId(loginId)
        if(user?.role != Role.ADMIN) {
            throw IllegalArgumentException("관리자만 공지사항을 작성할 수 있습니다.")
        }
        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        var notice = NoticeDto(user = user,noticeDate = datetime, noticeTitle = noticeTitle,noticeContent = noticeContent).asEntity()
        noticeRepository.save(notice)
        if(files != null)
        {
            files.forEach{file->
                val originalFileName = file.originalFilename
                val saveFileName = getSaveFileName(originalFileName)
                val objMeta = ObjectMetadata()
                objMeta.contentLength = file.inputStream.available().toLong()
                amazonS3.putObject(bucket,saveFileName,file.inputStream , objMeta)

                val fileUrl = amazonS3.getUrl(bucket, saveFileName).toString()
                val noticeFile = NoticeFileDto(fileUrl,originalFileName ?: "첨부파일" , notice )
                noticeFileRepository.save(noticeFile.asEntity())
            }
        }
    }

    //공지사항 수정
    @Transactional
    fun updateNotice(loginId: String, noticeId: Long, noticeTitle: String, noticeContent: String,delFileIds: List<Long>? ,files: List<MultipartFile>?){
        var notice = findNoticeById(noticeId)
        val user = findUserByLoginId(loginId)
        if(user?.userId != notice.user.userId){
            throw IllegalArgumentException("작성자만 수정할 수 있습니다.")
        }
        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        notice.noticeDate = datetime
        notice.noticeTitle = noticeTitle
        notice.noticeContent = noticeContent
        if(delFileIds != null)
        {
            delFileIds.forEach{fileId->
                val noticeFile = noticeFileRepository.findByFileId(fileId)
                delFile(noticeFile.fileUrl)
                noticeFileRepository.delete(noticeFile)
            }
        }
        if(files != null)
        {
            files.forEach{file->
                val originalFileName = file.originalFilename
                val saveFileName = getSaveFileName(originalFileName)
                val objMeta = ObjectMetadata()
                objMeta.contentLength = file.inputStream.available().toLong()
                amazonS3.putObject(bucket,saveFileName,file.inputStream , objMeta)

                val fileUrl = amazonS3.getUrl(bucket, saveFileName).toString()
                val noticeFile = NoticeFileDto(fileUrl,originalFileName ?: "첨부파일" , notice )
                noticeFileRepository.save(noticeFile.asEntity())
            }
        }
    }

    //공지사항 삭제
    @Transactional
    fun deleteNotice(loginId: String,noticeId: Long){
        val user = findUserByLoginId(loginId)
        val notice = findNoticeById(noticeId)
        if(user?.userId != notice.user.userId){
            throw IllegalArgumentException("작성자만 삭제할 수 있습니다.")
        }
        val noticeFiles = noticeFileRepository.findByNotice(notice)
        if(noticeFiles != null){
            noticeFiles.forEach { noticeFile ->
                delFile(noticeFile.fileUrl)
                noticeFileRepository.delete(noticeFile)
            }
        }
        noticeRepository.deleteById(noticeId)
    }

    //공지사항 제목으로 검색
    fun searchNoticeByTitle(page: Int, keyword: String): Page<NoticeInfoDto> {
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("noticeId").descending())
        return noticeRepository.findByNoticeTitleContaining(keyword,pageable).map { it.asInfoDto() }
    }

    //공지사항 내용으로 검색
    fun searchNoticeByContent(page: Int, keyword: String): Page<NoticeInfoDto> {
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("noticeId").descending())
        return noticeRepository.findByNoticeContentContaining(keyword,pageable).map { it.asInfoDto() }
    }

    //로그인 아이디로 유저 검색, 없으면 예외처리
    fun findUserByLoginId(loginId: String): UserEntity?{
        val user = userRepository.findByLoginId(loginId)
                ?: throw NoSuchElementException("Invalid loginId : $loginId 존재하지 않는 아이디입니다.")
        return user
    }

    //공지사항 id로 공지 검색, 없으면 예외처리
    fun findNoticeById(noticeId: Long): NoticeEntity{
        val notice = noticeRepository.findById(noticeId).orElseThrow {
            NoSuchElementException("Invalid noticeId : $noticeId 존재하지 않는 게시글입니다.")
        }
        return notice
    }

    //첨부파일 다운로드시 uuid를 제거한 후 파일 다운로드
    fun fileDownload(fileId : Long) :ResponseEntity<ByteArray>
    {
        val file = noticeFileRepository.findByFileId(fileId)
        val splitStr = ".com/"
        val s3FileName = file.fileUrl.substring(file.fileUrl.lastIndexOf(splitStr) + splitStr.length)
        val decodeFile = URLDecoder.decode(s3FileName,"UTF-8")
        val obj = amazonS3.getObject(bucket, decodeFile)

        val objectInputStream = obj.objectContent
        val bytes = IOUtils.toByteArray(objectInputStream)

        val fileName = URLEncoder.encode(file.fileName, "UTF-8").replace("+", "%20")
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM
        httpHeaders.contentLength = bytes.size.toLong()
        httpHeaders.setContentDispositionFormData("attachment", fileName)

        return ResponseEntity(bytes, httpHeaders, HttpStatus.OK)
    }

    private fun delFile(fileUrl:String)
    {
        val splitStr = ".com/"
        val fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length)
        val decodeFile = URLDecoder.decode(fileName,"UTF-8")
        amazonS3.deleteObject(bucket, decodeFile)
    }
    private fun getSaveFileName(originalFilename: String?): String {
        return UUID.randomUUID().toString() + "-" + originalFilename
    }


}