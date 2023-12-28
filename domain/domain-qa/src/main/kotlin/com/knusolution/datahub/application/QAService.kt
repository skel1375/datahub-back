package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class QAService(
    private val qaRepository: QARepository,
    private val replyRepository: ReplyRepository,
    private val userRepository: UserRepository
){
    val pageSize = 10

    fun getQaPage():Int
    {
        val qas=qaRepository.findAll()
        val allPage = if (qas.size % pageSize == 0) {
            qas.size / pageSize
        } else {
            qas.size / pageSize + 1
        }

        return allPage
    }
    fun getQa(page : Int):List<QAEntity>
    {
        val qas = qaRepository.findAll().reversed()

        val startIndex=(page-1)*pageSize
        if (startIndex >= qas.size) {
            return emptyList()
        }
        val endIndex = startIndex + pageSize
        return qas.subList(startIndex,minOf(endIndex,qas.size))
    }

    fun saveQa(userId : Long, qaTitle: String, qaContent: String)
    {
        val existUser = userRepository.findById(userId)
        val user = existUser.get()
        val datetime= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val qa=QADto(qaTitle = qaTitle, qaDate = datetime, qaContent = qaContent, userId = user)

        qaRepository.save(qa.asEntity())
    }


    fun updateQa(userId: Long,qaId: Long,updateTitle:String,updateContent:String): Boolean
    {
        val existUser = userRepository.findById(userId)
        val user = existUser.get()
        val existQa = qaRepository.findById(qaId)
        val qa = existQa.get()
        if(user.userId == qa.userId.userId)
        {
            qa.qaContent=updateContent
            qa.qaTitle=updateTitle
            qaRepository.save(qa)
            return true
        }
        return false
    }

    fun delQa(userId:Long,qaId: Long):Boolean
    {
        val existUser = userRepository.findById(userId)
        val user = existUser.get()
        val existQa = qaRepository.findById(qaId)
        val qa = existQa.get()

        if(user.userId == qa.userId.userId || user.userId == 1L) {
            qaRepository.delete(qa)
            return true
        }
        return false
    }

    fun getQabyId(qaId: Long):QAEntity
    {
        val existQa = qaRepository.findById(qaId)
        val qa = existQa.get()

        return qa
    }

    fun getReply(qaId: Long) : List<ReplyEntity>
    {
        val existingQa=qaRepository.findById(qaId)
        val qa=existingQa.get()
        val replys=replyRepository.findByQaId(qa)
        return replys
    }

    fun saveReply(userId : Long, qaId: Long ,replyContent:String)
    {
        val existUser = userRepository.findById(userId)
        val user = existUser.get()
        val existQa = qaRepository.findById(qaId)
        val qa = existQa.get()
        val datetime= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val reply=ReplyDto(replyDate = datetime,replyContent=replyContent, userId = user, qaId = qa)

        replyRepository.save(reply.asEntity())
    }

    fun updateReply(userId: Long,replyId: Long,updateContent: String):Boolean
    {
        val existUser = userRepository.findById(userId)
        val user = existUser.get()
        val existReply = replyRepository.findById(replyId)
        val reply = existReply.get()

        if(user.userId == reply.userId.userId) {
            reply.replyContent = updateContent
            replyRepository.save(reply)
            return true
        }
        return false
    }

    fun delReply(userId: Long,replyId :Long):Boolean
    {
        val existUser = userRepository.findById(userId)
        val user = existUser.get()
        val existReply = replyRepository.findById(replyId)
        val reply = existReply.get()

        if(user.userId ==reply.userId.userId || user.userId == 1L) {
            replyRepository.delete(reply)
            return true
        }
        return false
    }
}