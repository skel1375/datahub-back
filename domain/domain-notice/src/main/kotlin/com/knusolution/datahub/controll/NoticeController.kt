package com.knusolution.datahub.controll

import com.knusolution.datahub.application.NoticeService
import com.knusolution.datahub.domain.NoticeInfoResponse
import com.knusolution.datahub.domain.NoticeModalResponse
import com.knusolution.datahub.domain.asInfoDto
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
        noticeService.saveNotice(request.userId,request.noticeTitle,request.noticeContent)
    }

    @PutMapping("notice/update")
    fun updateNotice(@RequestBody request:UpdateNoticeRequest) {
        noticeService.updateNotice(request.userId,request.noticeId,request.noticeTitle,request.noticeContent)
    }

    @DeleteMapping("notice/delete")
    fun deleteNotice(@RequestBody request: DeleteNoticeRequest) {
        noticeService.deleteNotice(request.userId, request.noticeId)
    }
}

data class SaveNoticeRequest(
        val userId: Long,
        val noticeTitle: String,
        val noticeContent: String
)

data class UpdateNoticeRequest(
        val userId: Long,
        val noticeId: Long,
        val noticeTitle: String,
        val noticeContent: String
)

data class DeleteNoticeRequest(
        val userId: Long,
        val noticeId: Long
)