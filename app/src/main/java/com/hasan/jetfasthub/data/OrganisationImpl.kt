package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RetrofitInstance
import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModel
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface OrganisationRepository{

    suspend fun getOrganisationMembers(token: String, organisation: String, page: Int): Response<OrganisationMemberModel>

    suspend fun getOrganisationRepos(token: String, organisation: String, page: Int, type: String): Response<OrganisationsRepositoryModel>

}


class OrganisationImpl(private val context: Context) : OrganisationRepository {

    override suspend fun getOrganisationMembers(token: String, organisation: String, page: Int): Response<OrganisationMemberModel> {
        return RetrofitInstance(context = context).gitHubService.getOrganisationMembers(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            organisation = organisation,
            page = page
        )
    }

    override suspend fun getOrganisationRepos(
        token: String,
        organisation: String,
        page: Int,
        type: String
    ): Response<OrganisationsRepositoryModel> {
        return RetrofitInstance(context).gitHubService.getOrganisationsRepositories(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            org = organisation,
            page = page,
            type = type
        )
    }

}