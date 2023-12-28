package com.knusolution.datahub.controll

import com.knusolution.datahub.application.QAService
import com.knusolution.datahub.domain.QAInfoResponse
import com.knusolution.datahub.domain.ReplyResponse
import com.knusolution.datahub.domain.asDto
import com.knusolution.datahub.domain.asInfoDto
import org.apache.el.parser.BooleanNode
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class QAController(
    private val qaService : QAService
){
    @GetMapping("/qas")
    fun getQas(
        @RequestParam page:Int
    ): QAInfoResponse
    {
        val allPage = qaService.getQaPage()
        val qas= qaService.getQa(page).map{it.asInfoDto()}
        return QAInfoResponse(allPage=allPage, page=page, qas=qas)
    }

    @PostMapping("/addition-qa")
    fun saveQa(
        @RequestParam userId:Long,
        @RequestParam qaTitle:String,
        @RequestParam qaContent:String
    )
    {
        qaService.saveQa(userId,qaTitle,qaContent)
    }

    @PostMapping("/modi-qa")
    fun updateQa(
        @RequestParam userId: Long,
        @RequestParam qaId: Long,
        @RequestParam updateTitle: String,
        @RequestParam updateContent: String,
    ):Boolean
    {
        return qaService.updateQa(userId,qaId,updateTitle,updateContent)
    }

    @PostMapping("/del-qa")
    fun delQa(
        @RequestParam userId: Long,
        @RequestParam qaId: Long
    ):Boolean
    {
        return qaService.delQa(userId,qaId)
    }

    @GetMapping("/replys")
    fun getQaContentReply(
        @RequestParam qaId: Long
    ): ReplyResponse
    {
        val qa=qaService.getQabyId(qaId).asInfoDto()
        val content= qaService.getQabyId(qaId).asDto().qaContent
        val replys= qaService.getReply(qaId).map{it.asInfoDto()}

        return ReplyResponse(qa=qa,content=content, replys = replys)
    }

    @PostMapping("/addition-reply")
    fun saveReply(
        @RequestParam userId:Long,
        @RequestParam qaId:Long,
        @RequestParam replyContent:String
    )
    {
        qaService.saveReply(userId = userId, qaId = qaId, replyContent = replyContent)
    }

    @PostMapping("/modi-reply")
    fun updateReply(
        @RequestParam userId: Long,
        @RequestParam replyId:Long,
        @RequestParam updateContent: String
    ):Boolean
    {
        return qaService.updateReply(userId=userId,replyId = replyId, updateContent = updateContent)
    }

    @PostMapping("/del-reply")
    fun delReply(
        @RequestParam userId: Long,
        @RequestParam replyId: Long
    ):Boolean
    {
        return qaService.delReply(userId=userId,replyId = replyId)
    }
}