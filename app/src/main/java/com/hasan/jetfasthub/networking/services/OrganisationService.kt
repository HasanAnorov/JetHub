package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModel
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModel
import com.hasan.jetfasthub.screens.main.organisations.organisation_model.OrganisationModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface OrganisationService {

    @Headers("Accept: application/vnd.github+json")
    @GET("orgs/{org}/members")
    suspend fun getOrganisationMembers(
        @Header("Authorization") authToken: String,
        @Path("org") organisation: String,
        @Query("page") page: Int
    ): Response<OrganisationMemberModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("orgs/{org}/repos")
    suspend fun getOrganisationsRepositories(
        @Header("Authorization") authToken: String,
        @Path("org") org: String,
        @Query("type") type: String,
        @Query("page") page: Int
    ): Response<OrganisationsRepositoryModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("orgs/{org}")
    suspend fun getOrganisation(
        @Header("Authorization") authToken: String,
        @Path("org") org: String,
    ): Response<OrganisationModel>

}