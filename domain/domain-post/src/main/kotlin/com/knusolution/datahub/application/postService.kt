package com.knusolution.datahub.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.knusolution.datahub.domain.*
import com.knusolution.datahub.system.domain.BaseCategoryRepository
import com.knusolution.datahub.system.domain.DetailCategoryRepository
import com.knusolution.datahub.system.domain.SystemRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.net.URLDecoder


@Service
class PostService(
    private val articleRepository: ArticleRepository,
    private val detailCategoryRepository: DetailCategoryRepository,
    private val systemRepository: SystemRepository,
    private val baseCategoryRepository: BaseCategoryRepository,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    private val amazonS3: AmazonS3
){
    val pageSize=10

    //대기중 게시물
    fun getWaitArticles(page : Int) : Page<WaitArticleDto>
    {
        val approval = "대기"
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("articleId").descending())
        articleRepository.findAll(pageable)
        return articleRepository.findAllByApproval(approval,pageable).map{
            val detailCategoryName = it.detailCategory.detailCategoryName
            val systemName = it.detailCategory.baseCategory.system.systemName
            it.asWaitDto(systemName,detailCategoryName)
        }
    }

    fun getArticles(detailCategoryId: Long,page: Int):Page<ArticleInfoDto>
    {
        val detailCategory = detailCategoryRepository.findByDetailCategoryId(detailCategoryId)
        val pageable = PageRequest.of(page-1,pageSize, Sort.by("articleId").descending())

        return articleRepository.findAllByDetailCategory(detailCategory,pageable).map { it.asInfoDto() }
    }
    fun saveArticle( detailCategoryId : Long , file : MultipartFile){
        val existingDetailCategory = detailCategoryRepository.findById(detailCategoryId)
        val detailCategory = existingDetailCategory.get()
        val originalFileName = file.originalFilename
        val saveFileName = getSaveFileName(originalFileName)
        val objMeta = ObjectMetadata()
        objMeta.contentLength = file.inputStream.available().toLong()
        amazonS3.putObject(bucket,saveFileName,file.inputStream , objMeta)

        val fileUrl = amazonS3.getUrl(bucket, saveFileName).toString()

        val datetime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val article = ArticleDto( datetime , "대기" ,"",fileUrl,originalFileName ?: detailCategory.detailCategoryName ,"","",detailCategory)
        articleRepository.save(article.asEntity())
    }
    fun postDeclineFile(articleId : Long , approval : String , declineDetail : String? , file : MultipartFile?){
        val article = articleRepository.findByArticleId(articleId)
        article.approval = approval
        if(approval == "반려" && file != null && declineDetail!= null ) {
            val originalFileName = file.originalFilename
            val saveFileName = getSaveFileName(originalFileName)
            val objMeta = ObjectMetadata()
            objMeta.contentLength = file.inputStream.available().toLong()
            amazonS3.putObject(bucket,saveFileName,file.inputStream , objMeta)

            val fileUrl = amazonS3.getUrl(bucket, saveFileName).toString()

            article.declineDetail = declineDetail
            article.declineFileName = originalFileName ?: "declineFile"
            article.declineFileUrl = fileUrl

        }
        articleRepository.save(article)
    }

    fun updateArticle(articleId:Long,approval: String,declineDetail: String?,file: MultipartFile?)
    {
        val article = articleRepository.findByArticleId(articleId)
        if(article.approval == "승인") {
            if (approval == "반려") {
                if (file == null)
                    throw IllegalArgumentException("File cannot be null for article rejection.")
                postDeclineFile(articleId, approval, declineDetail, file)
            }
        }
        else
        {
            if(approval == "승인")
            {
                delFile(article.declineFileUrl)
                article.approval = approval
                article.declineFileUrl = ""
                article.declineDetail=""
                article.declineFileName=""
                articleRepository.save(article)
            }
            else
            {
                if(file != null)
                {
                    delFile(article.declineFileUrl)
                    postDeclineFile(articleId, approval, declineDetail, file)
                }
                else if(declineDetail != null) {
                    article.declineDetail = declineDetail
                    articleRepository.save(article)
                }
            }
        }

    }
    fun delArticle(articleId : Long)
    {
        val article = articleRepository.findByArticleId(articleId)
        delFile(article.taskFileUrl)
        if(article.approval == "반려")
        {
            delFile(article.declineFileUrl)
        }
        articleRepository.delete(article)
    }

    fun delAllArticle(systemId:Long)
    {
        val system = systemRepository.findBySystemId(systemId)
            val baseCategorys = baseCategoryRepository.findAllBySystemSystemId(system.systemId)
            baseCategorys.forEach{baseCategory->
                val detailCategorys = detailCategoryRepository.findAllByBaseCategoryBaseCategoryId(baseCategory.baseCategoryId)
                detailCategorys.forEach{detailCategory->
                    val articles = articleRepository.findByDetailCategory(detailCategory)
                    articles.forEach{article->
                        delFile(article.taskFileUrl)
                        if(article.declineFileUrl != "") {
                            delFile(article.declineFileUrl)
                        }
                        articleRepository.delete(article)
                    }
                    detailCategoryRepository.delete(detailCategory)
                }
                baseCategoryRepository.delete(baseCategory)
            }
    }

    private fun getSaveFileName(originalFilename: String?): String {
        return UUID.randomUUID().toString() + "-" + originalFilename
    }

    private fun delFile(fileUrl:String)
    {
        val splitStr = ".com/"
        val fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length)
        val decodeTaskFile = URLDecoder.decode(fileName,"UTF-8")
        amazonS3.deleteObject(bucket, decodeTaskFile)
    }

}