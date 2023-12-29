package com.knusolution.datahub.controll

import com.knusolution.datahub.application.NoticeService
import com.knusolution.datahub.domain.NoticeDto
import com.knusolution.datahub.domain.NoticeResponse
import com.knusolution.datahub.domain.asInfoDto
import org.springframework.web.bind.annotation.*

@RestController
class NoticeController(
        private val noticeService: NoticeService
) {
    @GetMapping("/notices")
    fun getNotices(
            @RequestParam page: Int
    ): NoticeResponse? {
        val allPage = noticeService.getNoticePage()
        val notices = noticeService.getNotice(page).map { it.asInfoDto() }
        return NoticeResponse(allPage = allPage, page = page, notices = notices)
    }
    @PostMapping("/notice/post")
    fun saveNotice(@RequestBody request: SaveNoticeRequest) {
        try {
            noticeService.saveNotice(request.userId,request.noticeTitle,request.noticeContent)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("관리자만 공지사항을 작성할 수 있습니다.")
        }
    }

    @PutMapping("notice/update")
    fun updateNotice(@RequestBody request:UpdateNoticeRequest) {
        try {
            noticeService.updateNotice(request.userId,request.noticeId,request.noticeTitle,request.noticeContent)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("작성자만 수정할 수 있습니다.")
        }
    }

    @DeleteMapping("notice/delete")
    fun deleteNotice(@RequestBody request: DeleteNoticeRequest) {
       try{
           noticeService.deleteNotice(request.userId, request.noticeId)
       } catch (e: IllegalArgumentException){
           throw IllegalArgumentException("작성자만 삭제할 수 있습니다.")
       }
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