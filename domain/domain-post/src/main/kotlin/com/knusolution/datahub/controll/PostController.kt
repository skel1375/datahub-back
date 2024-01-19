package com.knusolution.datahub.controll

import com.knusolution.datahub.application.PostService
import com.knusolution.datahub.domain.ArticleInfoDto
import com.knusolution.datahub.domain.WaitArticleDto
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class PostController(
    private val postService : PostService
){
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/wait-article")
    fun waitArticles(@RequestParam page:Int): Page<WaitArticleDto>
    {
        return postService.getWaitArticles(page)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/articles")
    fun getArticles(
        @RequestParam detailCategoryId: Long,
        @RequestParam page: Int
    ): Page<ArticleInfoDto>
    {
        return postService.getArticles(detailCategoryId,page)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/article-file")
    fun postArticle(
        @RequestParam detailCategoryId : Long,
        @RequestPart file : MultipartFile
    ){
        postService.saveArticle(detailCategoryId, file)
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/article-review")
    fun postDeclineFile(
        @RequestParam articleId : Long,
        @RequestParam approval : String,
        @RequestParam(required = false) declineDetail :String?,
        @RequestPart(required = false) file : MultipartFile?
    ){
        postService.postDeclineFile(articleId, approval, declineDetail, file)
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/article/del-all")
    fun delAllArticle(
        @RequestParam systemId:Long)
    {
        postService.delAllArticle(systemId)
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/article/update")
    fun updateArticle(
        @RequestParam articleId : Long,
        @RequestParam approval : String,
        @RequestParam(required = false) declineDetail :String?,
        @RequestPart(required = false) file : MultipartFile?,
        @RequestParam isFileChange : Boolean
    )
    {
        postService.updateArticle(articleId, approval, declineDetail, file, isFileChange)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/article/del")
    fun delWaitArticle(
        @RequestParam articleId: Long
    )
    {
        postService.delArticle(articleId)
    }

}

