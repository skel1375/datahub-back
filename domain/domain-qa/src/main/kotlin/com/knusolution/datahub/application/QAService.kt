package com.knusolution.datahub.application

import com.knusolution.datahub.domain.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
    //Qa 리스트 전달
    fun getQa(page:Int): Page<QAInfoDto>
    {
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("QaId").descending())
        return qaRepository.findAll(pageable).map{it.asInfoDto()}
    }
    //QA 제목으로 검색
    fun searchQaByTitle(page: Int, keyword:String):Page<QAInfoDto>?
    {
        val pageable = PageRequest.of(page-1,pageSize,Sort.by("QaId").descending())
        return qaRepository.findByQaTitleContaining(keyword,pageable).map { it.asInfoDto() }
    }
    //QA내용으로 검색
    fun searchQaByContent(page: Int, keyword:String):Page<QAInfoDto>?
    {
        val pageable = PageRequest.of(page-1,pageSize,Sort.by("QaId").descending())
        return qaRepository.findByQaContentContaining(keyword,pageable).map { it.asInfoDto() }
    }
    //QA작성자로 검색
    fun searchQaByWriter(page:Int, keyword: String):Page<QAInfoDto>?
    {
        val pageable = PageRequest.of(page-1,pageSize,Sort.by("QaId").descending())
        return qaRepository.findByUserLoginIdContaining(keyword,pageable).map { it.asInfoDto() }
    }
    //QA작성
    fun saveQa(loginId : String, qaTitle: String, qaContent: String)
    {
        val user = userRepository.findByLoginId(loginId)
        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val qa= user?.let { QADto(qaTitle = qaTitle, qaDate = datetime, qaContent = qaContent, user = it) }

        if (qa != null) {
            qaRepository.save(qa.asEntity())
        }
    }
    //QA수정
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
    //QA삭제
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
    //QA에 해당하는 답글 리스트
    fun getReply(qaId: Long) : List<ReplyInfoDto>
    {
        val qa=getQabyId(qaId)
        val replys=replyRepository.findByQa(qa)
        return replys.map{it.asInfoDto()}
    }
    //답글 저장
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
    //답글수정
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
    //답글삭제
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
    //system과user에 연결된 모든 답글과 QA삭제
    fun delAllQaReply(systemId: Long)
    {
        val userSystem= userSystemRepository.findBySystemSystemId(systemId)
        val user = userSystem.user
        val replys =  replyRepository.findByUser(user)
        if (replys != null) {
            replyRepository.deleteAllInBatch(replys)
        }
        val qas = qaRepository.findByUser(user)
        if(qas != null){
            qas.forEach{ qa->
                delQa(user.loginId,qa.qaId)
            }
        }
    }

}