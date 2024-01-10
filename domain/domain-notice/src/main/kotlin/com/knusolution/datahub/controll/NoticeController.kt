package com.knusolution.datahub.controll

import com.knusolution.datahub.application.NoticeService
import com.knusolution.datahub.domain.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class NoticeController(
        private val noticeService: NoticeService
) {
    //게시물 정보, 페이징
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/notices")
    fun getNotices(@RequestParam page: Int): NoticeInfoResponse? {
        val allPage = noticeService.getNoticePage()
        val notices = noticeService.getNotice(page).map { it.asInfoDto() }
        return NoticeInfoResponse(allPage = allPage, page = page, notices = notices)
    }
    //게시물 조회
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/notice")
    fun getNotice(@RequestParam noticeId: Long): NoticeModalResponse? {
        return noticeService.getNoticeData(noticeId)
    }
    //게시물 작성
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/notice/post")
    fun saveNotice(@RequestBody request: SaveNoticeRequest) {
        noticeService.saveNotice(request.loginId,request.noticeTitle,request.noticeContent)
    }
    //게시물 수정
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("notice/update")
    fun updateNotice(@RequestBody request:UpdateNoticeRequest) {
        noticeService.updateNotice(request.loginId,request.noticeId,request.noticeTitle,request.noticeContent)
    }
    //게시물 삭제
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("notice/delete")
    fun deleteNotice(@RequestBody request: DeleteNoticeRequest) {
        noticeService.deleteNotice(request.loginId, request.noticeId)
    }
}