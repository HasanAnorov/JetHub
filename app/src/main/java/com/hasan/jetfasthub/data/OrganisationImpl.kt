package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModel
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModel
import com.hasan.jetfasthub.screens.main.organisations.organisation_model.OrganisationModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface OrganisationRepository {

    suspend fun getOrganisationMembers(
        token: String,
        organisation: String,
        page: Int
    ): Response<OrganisationMemberModel>

    suspend fun getOrganisation(token: String, organisation: String): Response<OrganisationModel>

    suspend fun getOrganisationRepos(
        token: String,
        organisation: String,
        page: Int,
        type: String
    ): Response<OrganisationsRepositoryModel>

}


class OrganisationImpl(private val context: Context) : OrganisationRepository {

    override suspend fun getOrganisation(
        token: String,
        organisation: String
    ): Response<OrganisationModel> {
        return RestClient(context).organisationService.getOrganisation(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            org = organisation
        )
    }

    override suspend fun getOrganisationMembers(
        token: String,
        organisation: String,
        page: Int
    ): Response<OrganisationMemberModel> {
        return RestClient(context = context).organisationService.getOrganisationMembers(
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
        return RestClient(context).organisationService.getOrganisationsRepositories(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            org = organisation,
            page = page,
            type = type
        )
    }

}