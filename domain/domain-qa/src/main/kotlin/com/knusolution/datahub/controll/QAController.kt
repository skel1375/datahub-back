package com.knusolution.datahub.controll

import com.knusolution.datahub.application.QAService
import com.knusolution.datahub.domain.*
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class QAController(
    private val qaService : QAService
){
    //QA리스트 전달
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @GetMapping("/qa")
    fun getQas(
        @RequestParam page:Int
    ):Page<QAInfoDto>
    {
        return qaService.getQa(page)
    }
    //QA검색
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @GetMapping("/qa/search")
    fun searchQA(
        @RequestParam page:Int,
        @RequestParam keyword:String,
        @RequestParam searchBy:String
    ): Page<QAInfoDto>?
    {
        when (searchBy) {
            "title" -> {
                return qaService.searchQaByTitle(page,keyword)
            }
            "content" -> {
                return qaService.searchQaByContent(page,keyword)
            }
            "loginId" -> {
                return qaService.searchQaByWriter(page, keyword)
            }
        }
        throw IllegalArgumentException("searchBy 값을 다시 확인해주세요.")
    }
    //QA저장
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @PostMapping("/qa/post")
    fun saveQa(
        @RequestParam loginId:String,
        @RequestParam qaTitle:String,
        @RequestParam qaContent:String
    )
    {
        qaService.saveQa(loginId,qaTitle,qaContent)
    }
    //QA수정
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @PutMapping("/qa/update")
    fun updateQa(
        @RequestParam loginId: String,
        @RequestParam qaId: Long,
        @RequestParam updateTitle: String,
        @RequestParam updateContent: String,
    ):Boolean
    {
        return qaService.updateQa(loginId,qaId,updateTitle,updateContent)
    }
    //QA삭제
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @DeleteMapping("/qa/del")
    fun delQa(
        @RequestParam loginId: String,
        @RequestParam qaId: Long
    ):Boolean
    {
        return qaService.delQa(loginId,qaId)
    }
    //QA에 해당하는 답글 리스트
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @GetMapping("/qa/content")
    fun getQaContentReply(
        @RequestParam qaId: Long
    ): ReplyResponse
    {
        val qa=qaService.getQabyId(qaId).asInfoDto()
        val content= qaService.getQabyId(qaId).asDto().qaContent
        val replys= qaService.getReply(qaId)

        return ReplyResponse(qa=qa,content=content, replys = replys)
    }
    //답글 추가
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @PostMapping("/reply/addition")
    fun saveReply(
        @RequestParam loginId: String,
        @RequestParam qaId:Long,
        @RequestParam replyContent:String
    )
    {
        qaService.saveReply(loginId = loginId, qaId = qaId, replyContent = replyContent)
    }
    //답글 수정
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @PutMapping("/reply/update")
    fun updateReply(
        @RequestParam loginId: String,
        @RequestParam replyId:Long,
        @RequestParam updateContent: String
    ):Boolean
    {
        return qaService.updateReply(loginId = loginId,replyId = replyId, updateContent = updateContent)
    }
    //답글 삭제
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @DeleteMapping("/reply/del")
    fun delReply(
        @RequestParam loginId: String,
        @RequestParam replyId: Long
    ):Boolean
    {
        return qaService.delReply(loginId = loginId ,replyId = replyId)
    }
    //system과user에 연결된 모든 QA와 답글 삭제
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @DeleteMapping("/qa/clear")
    fun delAllQaReply(@RequestParam systemId:Long)
    {
        qaService.delAllQaReply(systemId)
    }
}