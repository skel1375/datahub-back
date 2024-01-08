package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val userRepository: UserRepository,
    private val userSystemRepository: UserSystemRepository
) {
    val pageSize = 10
    fun getNoticePage():Int
    {
        val notices = noticeRepository.findAll()
        val allPage = if(notices.size % pageSize == 0){
            notices.size / pageSize
        } else {
            notices.size / pageSize + 1
        }
        return allPage
    }

    //해당 페이지에 있는 공지사항을 리스트로 반환
    @Transactional(readOnly = true)
    fun getNotice(page: Int): List<NoticeEntity> {
       val notices = noticeRepository.findAll().reversed()

        val startIndex = (page-1) * pageSize
        if(startIndex >= notices.size){
            return emptyList()
        }
        val endIndex = startIndex + pageSize
        return notices.subList(startIndex, minOf(endIndex, notices.size))
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