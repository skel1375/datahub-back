package com.knusolution.datahub.controll

import com.knusolution.datahub.application.NoticeService
import com.knusolution.datahub.domain.NoticeDto
import org.springframework.web.bind.annotation.*

@RestController
class NoticeController(private val noticeService: NoticeService) {
    @PostMapping("/notices")
    fun createNotice(@RequestBody noticeDto: NoticeDto): NoticeDto {
        return noticeService.createNotice(noticeDto)
    }

    @GetMapping("/notices/{noticeId}")
    fun getNotice(@PathVariable noticeId: Long): NoticeDto {
        return noticeService.getNotice(noticeId)
    }
}