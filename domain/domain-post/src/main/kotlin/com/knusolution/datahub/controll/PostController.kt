package com.knusolution.datahub.controll

import com.knusolution.datahub.domain.ArticleResponse
import com.knusolution.datahub.application.PostService
import com.knusolution.datahub.domain.asInfoDto
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class PostController(
    private val postService : PostService
){
    @GetMapping("/wait-article")
    fun waitArticles(@RequestParam page:Int): ArticleResponse?
    {
        val allpage = postService.getWaitPage()
        val articles = postService.getWaitArticles(page).map{it.asInfoDto()}
        return ArticleResponse(allPage = allpage, page = page, articles = articles)
    }
    @GetMapping("/articles")
    fun getArticles(
        @RequestParam detailCategoryId: Long,
        @RequestParam page: Int
    ): ArticleResponse?
    {
        val allpage = postService.getPage(detailCategoryId)
        val articles = postService.getArticles(detailCategoryId,page).map{it.asInfoDto()}

        return ArticleResponse(allPage = allpage, page = page, articles = articles)
    }

    @PostMapping("/article-file")
    fun postArticle(
        @RequestParam detailCategoryId : Long,
        @RequestPart file : MultipartFile
    ){
        postService.saveArticle(detailCategoryId, file)
    }
    @PutMapping("/article-review")
    fun postDeclineFile(
        @RequestParam articleId : Long,
        @RequestParam approval : String,
        @RequestParam(required = false) declineDetail :String?,
        @RequestPart(required = false) file : MultipartFile?
    ){
        postService.postDeclineFile(articleId, approval, declineDetail, file)
    }
}

