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
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/qa")
    fun getQas(
        @RequestParam page:Int
    ):Page<QAInfoDto>
    {
        return qaService.getQa(page)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/qa/addition")
    fun saveQa(
        @RequestParam loginId:String,
        @RequestParam qaTitle:String,
        @RequestParam qaContent:String
    )
    {
        qaService.saveQa(loginId,qaTitle,qaContent)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/qa/modi")
    fun updateQa(
        @RequestParam loginId: String,
        @RequestParam qaId: Long,
        @RequestParam updateTitle: String,
        @RequestParam updateContent: String,
    ):Boolean
    {
        return qaService.updateQa(loginId,qaId,updateTitle,updateContent)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/qa/del")
    fun delQa(
        @RequestParam loginId: String,
        @RequestParam qaId: Long
    ):Boolean
    {
        return qaService.delQa(loginId,qaId)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/reply")
    fun getQaContentReply(
        @RequestParam qaId: Long
    ): ReplyResponse
    {
        val qa=qaService.getQabyId(qaId).asInfoDto()
        val content= qaService.getQabyId(qaId).asDto().qaContent
        val replys= qaService.getReply(qaId)

        return ReplyResponse(qa=qa,content=content, replys = replys)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/reply/addition")
    fun saveReply(
        @RequestParam loginId: String,
        @RequestParam qaId:Long,
        @RequestParam replyContent:String
    )
    {
        qaService.saveReply(loginId = loginId, qaId = qaId, replyContent = replyContent)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/reply/modi")
    fun updateReply(
        @RequestParam loginId: String,
        @RequestParam replyId:Long,
        @RequestParam updateContent: String
    ):Boolean
    {
        return qaService.updateReply(loginId = loginId,replyId = replyId, updateContent = updateContent)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/reply/del")
    fun delReply(
        @RequestParam loginId: String,
        @RequestParam replyId: Long
    ):Boolean
    {
        return qaService.delReply(loginId = loginId ,replyId = replyId)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/qa-reply/del-all")
    fun delAllQaReply(@RequestParam systemId:Long)
    {
        qaService.delAllQaReply(systemId)
    }
}