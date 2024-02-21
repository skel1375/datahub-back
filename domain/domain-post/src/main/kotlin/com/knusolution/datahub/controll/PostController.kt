package com.knusolution.datahub.controll

import com.knusolution.datahub.application.PostService
import com.knusolution.datahub.domain.ArticleInfoDto
import com.knusolution.datahub.domain.WaitArticleDto
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class PostController(
    private val postService : PostService
){
    //대기중게시물
//    @PreAuthorize("hasAuthority('ADMIN')")
//    @GetMapping("/article/wait")
//    fun waitArticles(@RequestParam page:Int): Page<WaitArticleDto>
//    {
//        return postService.getWaitArticles(page)
//    }

    //세부카테고리의 게시물
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @GetMapping("/article")
    fun getArticles(
        @RequestParam detailCategoryId: Long,
        @RequestParam page: Int
    ): Page<ArticleInfoDto>
    {
        return postService.getArticles(detailCategoryId,page)
    }
    //게시글 추가
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @PostMapping("/article/task")
    fun postArticle(
        @RequestParam detailCategoryId : Long,
        @RequestPart file : MultipartFile
    ){
        postService.saveArticle(detailCategoryId, file)
    }
    //게시글의 승인여부와 관련파일 추가
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @PutMapping("/article/decline")
    fun postDeclineFile(
        @RequestParam articleId : Long,
        @RequestParam approval : String,
        @RequestParam(required = false) score: Int?,
        @RequestParam(required = false) declineDetail :String?,
        @RequestPart(required = false) file : MultipartFile?
    ){
        postService.postDeclineFile(articleId, approval,score,declineDetail, file)
    }
    //시스템과 관련된 모든 게시물삭제
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE')")
    @DeleteMapping("/article/clear")
    fun delAllArticle(
        @RequestParam systemId:Long)
    {
        postService.delAllArticle(systemId)
    }
    //게시글 수정
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE')")
    @PostMapping("/article/update")
    fun updateArticle(
        @RequestParam articleId : Long,
        @RequestParam approval : String,
        @RequestParam(required = false) score: Int?,
        @RequestParam(required = false) declineDetail :String?,
        @RequestPart(required = false) file : MultipartFile?,
    )
    {
        postService.updateArticle(articleId, approval,score ,declineDetail, file)
    }
    //게시글 삭제(유저느 대기중일때 삭제 가능)
    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @DeleteMapping("/article/del")
    fun delArticle(
        @RequestParam articleId: Long
    )
    {
        postService.delArticle(articleId)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MIDDLE','USER')")
    @GetMapping("/article/file/download")
    fun articleFileDownload(
        @RequestParam articleId: Long,
        @RequestParam fileType : String
    ): ResponseEntity<ByteArray>
    {

        if(fileType == "task")
            return postService.taskFileDownload(articleId)
        else if(fileType == "decline")
            return postService.declineFileDownload(articleId)
        else
            throw IllegalArgumentException("fileType입력 오류")
    }

}

