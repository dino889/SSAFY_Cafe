package com.ssafy.cafe.api


import com.ssafy.cafe.dto.Comment
import retrofit2.Call
import retrofit2.http.*

interface CommentApi {
    // comment를 추가한다.
    @POST("rest/comment")
    fun insert(@Body comment: Comment): Call<Boolean>

    // comment를 수정한다.
    @PUT("rest/comment")
    fun update(@Body comment: Comment): Call<Boolean>

    // {id}에 해당하는 comment를 삭제한다.
    @DELETE("rest/comment/{id}")
    fun delete(@Path("id") id: Int): Call<Boolean>

    // 사용자별 comment 정보를 조회한다.
    @GET("rest/comment/{userId}")
    fun selectCommentByUser(@Path("userId") userId : String) : Call<List<Comment>>

    // 사용자가 주문한 상품에 대해 7일 내에 리뷰를 작성하지 않은 내역이 있다면 반환한다.
    @GET("rest/comment/dupchk")
    fun notWrittenComm(@Query("userId") userId: String, @Query("productId") productId: Int) : Call<Int>

    // 리뷰 작성 여부 업데이트
    @PUT("rest/comment/dupchk")
    fun updateDupChk(@Query("dId") dId:Int) : Call<Boolean>

}