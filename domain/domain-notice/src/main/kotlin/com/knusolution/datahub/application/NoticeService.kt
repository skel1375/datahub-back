package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class NoticeService(
        private val noticeRepository: NoticeRepository,
        private val userRepository: UserRepository
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
    @Transactional
    fun saveNotice(userId: Long, noticeTitle: String, noticeContent: String) {
        val user = userRepository.findById(userId).get()
        if(user.role != Role.ADMIN) {
            throw IllegalArgumentException("관리자만 공지사항을 작성할 수 있습니다.")
        }
        val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val notice = NoticeDto(user = user,noticeDate = datetime, noticeTitle = noticeTitle,noticeContent = noticeContent).asEntity()
        noticeRepository.save(notice)
    }

    @Transactional
    fun updateNotice(userId: Long, noticeId: Long, noticeTitle: String, noticeContent: String){
        val notice = noticeRepository.findById(noticeId).get()
        val user = userRepository.findById(userId).get()
        if(user.userId != notice.user.userId){
            throw IllegalArgumentException("작성자만 수정할 수 있습니다.")
        }
        val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        notice.noticeDate = datetime
        notice.noticeTitle = noticeTitle
        notice.noticeContent = noticeContent
    }

    @Transactional
    fun deleteNotice(userId: Long,noticeId: Long){
        val user = userRepository.findById(userId).get()
        val notice = noticeRepository.findById(noticeId).get()
        if(user.userId != notice.user.userId){
            throw IllegalArgumentException("작성자만 삭제할 수 있습니다.")
        }
        noticeRepository.deleteById(noticeId)
    }
}