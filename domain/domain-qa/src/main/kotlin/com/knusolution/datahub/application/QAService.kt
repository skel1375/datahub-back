package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class QAService(
    private val qaRepository: QARepository,
    private val replyRepository: ReplyRepository,
    private val userRepository: UserRepository,
    private val userSystemRepository: UserSystemRepository,
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

    fun saveQa(loginId : String, qaTitle: String, qaContent: String)
    {
        val user = userRepository.findByLoginId(loginId)
        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val qa= user?.let { QADto(qaTitle = qaTitle, qaDate = datetime, qaContent = qaContent, user = it) }

        if (qa != null) {
            qaRepository.save(qa.asEntity())
        }
    }


    fun updateQa(loginId: String,qaId: Long,updateTitle:String,updateContent:String): Boolean
    {
        val user = userRepository.findByLoginId(loginId)
        val qa = qaRepository.findByQaId(qaId)
        if (user != null) {
            if(user.userId == qa.user.userId)
            {
                val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                qa.qaDate=datetime
                qa.qaContent=updateContent
                qa.qaTitle=updateTitle
                qaRepository.save(qa)
                return true
            }
        }
        return false
    }

    fun delQa(loginId: String,qaId: Long):Boolean
    {
        val user = userRepository.findByLoginId(loginId)
        val qa = qaRepository.findByQaId(qaId)

        if (user != null) {
            if(user.userId == qa.user.userId || user.userId == 1L) {
                val replies = replyRepository.findByQa(qa)
                replyRepository.deleteAllInBatch(replies)
                qaRepository.delete(qa)
                return true
            }
        }
        return false
    }

    fun getQabyId(qaId: Long):QAEntity
    {
        val qa = qaRepository.findByQaId(qaId)
        return qa
    }

    fun getReply(qaId: Long) : List<ReplyEntity>
    {
        val qa=getQabyId(qaId)
        val replys=replyRepository.findByQa(qa)
        return replys
    }

    fun saveReply(loginId: String, qaId: Long ,replyContent:String)
    {
        val user = userRepository.findByLoginId(loginId)
        val qa = qaRepository.findByQaId(qaId)
        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val reply= user?.let { ReplyDto(replyDate = datetime,replyContent=replyContent, user = it, qa = qa) }

        if (reply != null) {
            replyRepository.save(reply.asEntity())
        }
    }

    fun updateReply(loginId: String,replyId: Long,updateContent: String):Boolean
    {
        val user = userRepository.findByLoginId(loginId)
        val reply = replyRepository.findByReplyId(replyId)

        if (user != null) {
            if(user.userId == reply.user.userId) {
                val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                reply.replyDate=datetime
                reply.replyContent = updateContent
                replyRepository.save(reply)
                return true
            }
        }
        return false
    }

    fun delReply(loginId: String,replyId :Long):Boolean
    {
        val user = userRepository.findByLoginId(loginId)
        val reply = replyRepository.findByReplyId(replyId)

        if (user != null) {
            if(user.userId ==reply.user.userId || user.userId == 1L) {
                replyRepository.delete(reply)
                return true
            }
        }
        return false
    }

    fun delAllQaReply(systemId: Long)
    {
        val userSystems= userSystemRepository.findBySystemSystemId(systemId)
        val user = userSystems.firstOrNull { it.user.userId != 1L }?.user
        val replys = user?.let { replyRepository.findByUser(it) }
        if (replys != null) {
            replyRepository.deleteAllInBatch(replys)
        }
        val qas = user?.let { qaRepository.findByUser(it) }
        if(qas != null){
            qas.forEach{ qa->
                delQa(user.loginId,qa.qaId)
            }
        }
    }

}