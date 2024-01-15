package com.knusolution.datahub.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.knusolution.datahub.domain.*
import com.knusolution.datahub.system.domain.BaseCategoryRepository
import com.knusolution.datahub.system.domain.DetailCategoryRepository
import com.knusolution.datahub.system.domain.SystemRepository
import org.springframework.beans.factory.annotation.Value
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

    fun getWaitPage():Int
    {
        val aprove="대기"
        val articles=articleRepository.findByApproval(aprove)
        val allpage = if (articles.size % pageSize == 0) {
            articles.size / pageSize
        } else {
            articles.size / pageSize + 1
        }

        return allpage
    }

    fun getWaitArticles(page: Int): List<ArticleEntity>
    {
        val aprove="대기"
        val articles=articleRepository.findByApproval(aprove).reversed()

        val startIndex=(page-1)*pageSize
        if (startIndex >= articles.size) {
            return emptyList()
        }
        val endIndex = startIndex + pageSize
        return articles.subList(startIndex, minOf(endIndex, articles.size))
    }

    fun getArticles(detailCategoryId: Long,page: Int): List<ArticleEntity>{
        val existingDetailCategory = detailCategoryRepository.findById(detailCategoryId)
        val detailCategory = existingDetailCategory.get()
        val articles=articleRepository.findByDetailCategory(detailCategory).reversed()
        val startIndex=(page-1)*pageSize
        if (startIndex >= articles.size) {
            return emptyList()
        }
        val endIndex = startIndex + pageSize
        return articles.subList(startIndex, minOf(endIndex, articles.size))
    }
    fun getPage(detailCategoryId: Long): Int
    {
        val existingDetailCategory = detailCategoryRepository.findById(detailCategoryId)
        val detailCategory = existingDetailCategory.get()
        val articles=articleRepository.findByDetailCategory(detailCategory)
        val allpage = if (articles.size % pageSize == 0) {
            articles.size / pageSize
        } else {
            articles.size / pageSize + 1
        }

        return allpage
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

    //반려일때만 API사용가능?
    fun updateDecline(articleId: Long,declineDetail: String,file: MultipartFile)
    {
        val article = articleRepository.findByArticleId(articleId)
        delDeclineFile(article)

        val originalFileName = file.originalFilename
        val saveFileName = getSaveFileName(originalFileName)
        val objMeta = ObjectMetadata()
        objMeta.contentLength = file.inputStream.available().toLong()
        amazonS3.putObject(bucket,saveFileName,file.inputStream , objMeta)

        val fileUrl = amazonS3.getUrl(bucket, saveFileName).toString()

        article.declineDetail = declineDetail
        article.declineFileName = originalFileName ?: "declineFile"
        article.declineFileUrl = fileUrl
        articleRepository.save(article)
    }

    fun delWaitArticle(articleId : Long):Boolean
    {
        val article = articleRepository.findByArticleId(articleId)
        if(article.approval == "대기")
        {
            delTaskFile(article)
            articleRepository.delete(article)
            return true
        }
        return false
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
                        delTaskFile(article)
                        if(article.declineFileUrl != "") {
                            delDeclineFile(article)
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

    private fun delTaskFile(article : ArticleEntity)
    {
        val taskFileUrl = article.taskFileUrl
        val splitStr = ".com/"
        val taskFileName= taskFileUrl.substring(taskFileUrl.lastIndexOf(splitStr) + splitStr.length)
        val decodeTaskFile = URLDecoder.decode(taskFileName,"UTF-8")
        amazonS3.deleteObject(bucket, decodeTaskFile)
    }

    private fun delDeclineFile(article: ArticleEntity)
    {
        val declineFileUrl = article.declineFileUrl
        val splitStr = ".com/"
        val declineFileName =
            declineFileUrl.substring(declineFileUrl.lastIndexOf(splitStr) + splitStr.length)
        val decodeDeclineFile = URLDecoder.decode(declineFileName,"UTF-8")
        amazonS3.deleteObject(bucket, decodeDeclineFile)
    }

}