package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val userRepository: UserRepository,
) {
    val pageSize = 10

    fun getNotice(page:Int): Page<NoticeInfoDto> {
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("noticeId"))
        return noticeRepository.findAll(pageable).map{it.asInfoDto()}
    }

    //모달 띄울 때 정보 요청, 필요 시 사용
    @Transactional(readOnly = true)
    fun getNoticeData(noticeId: Long): NoticeModalResponse? {
        val notice = findNoticeById(noticeId)
        return NoticeModalResponse(notice.noticeTitle,notice.noticeContent)
    }

    @Transactional
    fun saveNotice(loginId: String, noticeTitle: String, noticeContent: String) {
        val user = findUserByLoginId(loginId)
        if(user?.role != Role.ADMIN) {
            throw IllegalArgumentException("관리자만 공지사항을 작성할 수 있습니다.")
        }
        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val notice = NoticeDto(user = user,noticeDate = datetime, noticeTitle = noticeTitle,noticeContent = noticeContent).asEntity()
        noticeRepository.save(notice)
    }

    @Transactional
    fun updateNotice(loginId: String, noticeId: Long, noticeTitle: String, noticeContent: String){
        val notice = findNoticeById(noticeId)
        val user = findUserByLoginId(loginId)
        if(user?.userId != notice.user.userId){
            throw IllegalArgumentException("작성자만 수정할 수 있습니다.")
        }
        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        notice.noticeDate = datetime
        notice.noticeTitle = noticeTitle
        notice.noticeContent = noticeContent
    }

    @Transactional
    fun deleteNotice(loginId: String,noticeId: Long){
        val user = findUserByLoginId(loginId)
        val notice = findNoticeById(noticeId)
        if(user?.userId != notice.user.userId){
            throw IllegalArgumentException("작성자만 삭제할 수 있습니다.")
        }
        noticeRepository.deleteById(noticeId)
    }

    fun searchNotice(page: Int, keyword: String): Page<NoticeInfoDto> {
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("noticeId"))
        return noticeRepository.findByNoticeTitleContaining(keyword,pageable).map { it.asInfoDto() }
    }
    fun findUserByLoginId(loginId: String): UserEntity?{
        val user = userRepository.findByLoginId(loginId)
                ?: throw NoSuchElementException("Invalid loginId : $loginId 존재하지 않는 아이디입니다.")
        return user
    }

    fun findNoticeById(noticeId: Long): NoticeEntity{
        val notice = noticeRepository.findById(noticeId).orElseThrow {
            NoSuchElementException("Invalid noticeId : $noticeId 존재하지 않는 게시글입니다.")
        }
        return notice
    }
}