package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeService(
        private val noticeRepository: NoticeRepository
) {
    @Transactional
    fun createNotice(noticeDto: NoticeDto): NoticeDto {
        val notice = noticeDto.asEntity()
        return noticeRepository.save(notice).asDto()
    }

    @Transactional(readOnly = true)
    fun getNotice(noticeId: Long): NoticeDto {
        val notice = noticeRepository.findById(noticeId).orElseThrow { IllegalArgumentException("Notice not found") }
        return notice.asDto()
    }
}