package com.knusolution.datahub.controll

import com.knusolution.datahub.application.NoticeService
import com.knusolution.datahub.domain.*
import org.springframework.web.bind.annotation.*

@RestController
class NoticeController(
        private val noticeService: NoticeService
) {
    @GetMapping("/notices")
    fun getNotices(@RequestParam page: Int): NoticeInfoResponse? {
        val allPage = noticeService.getNoticePage()
        val notices = noticeService.getNotice(page).map { it.asInfoDto() }
        return NoticeInfoResponse(allPage = allPage, page = page, notices = notices)
    }
    @GetMapping("/notice")
    fun getNotice(@RequestParam noticeId: Long): NoticeModalResponse? {
        return noticeService.getNoticeData(noticeId)
    }

    @PostMapping("/notice/post")
    fun saveNotice(@RequestBody request: SaveNoticeRequest) {
        noticeService.saveNotice(request.loginId,request.noticeTitle,request.noticeContent)
    }

    @PutMapping("notice/update")
    fun updateNotice(@RequestBody request:UpdateNoticeRequest) {
        noticeService.updateNotice(request.loginId,request.noticeId,request.noticeTitle,request.noticeContent)
    }

    @DeleteMapping("notice/delete")
    fun deleteNotice(@RequestBody request: DeleteNoticeRequest) {
        noticeService.deleteNotice(request.loginId, request.noticeId)
    }
}