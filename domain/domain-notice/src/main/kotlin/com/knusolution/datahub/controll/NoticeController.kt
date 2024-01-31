package com.knusolution.datahub.controll

import com.knusolution.datahub.application.NoticeService
import com.knusolution.datahub.domain.*
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class NoticeController(
        private val noticeService: NoticeService
) {
    //공지사항 리스트 전달
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/notice")
    fun getNotice(@RequestParam page:Int) : Page<NoticeInfoDto>
    {
        return noticeService.getNotice(page)
    }

    //공지사항 조회
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/notice/content")
    fun getNotice(@RequestParam noticeId: Long): NoticeModalResponse? {
        return noticeService.getNoticeData(noticeId)
    }

    //공지사항 작성
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/notice/post")
    fun saveNotice(@RequestBody request: SaveNoticeRequest) {
        noticeService.saveNotice(request.loginId,request.noticeTitle,request.noticeContent)
    }

    //게시물 수정
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/notice/update")
    fun updateNotice(@RequestBody request:UpdateNoticeRequest) {
        noticeService.updateNotice(request.loginId,request.noticeId,request.noticeTitle,request.noticeContent)
    }

    //공지사항 삭제
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/notice/delete")
    fun deleteNotice(@RequestBody request: DeleteNoticeRequest) {
        noticeService.deleteNotice(request.loginId, request.noticeId)
    }

    //공지사항 검색
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/notice/search")
    fun searchNotice(
        @RequestParam page: Int,
        @RequestParam keyword:String,
        @RequestParam searchBy:String):Page<NoticeInfoDto> {
        when(searchBy){
            "title" -> {
                return noticeService.searchNoticeByTitle(page,keyword)
            }
            "content" -> {
                return noticeService.searchNoticeByContent(page,keyword)
            }
        }
        throw IllegalArgumentException("SearchBy 값을 다시 확인해주세요.")
    }
}