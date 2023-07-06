package com.hasan.jetfasthub.screens.main.organisations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.OrganisationRepository
import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModel
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModel
import com.hasan.jetfasthub.screens.main.organisations.organisation_model.OrganisationModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrganisationsViewModel(private val repository: OrganisationRepository) : ViewModel() {

    private var _state: MutableStateFlow<OrganisationScreenState> = MutableStateFlow(
        OrganisationScreenState()
    )
    val state = _state.asStateFlow()

    fun getOrganisationMembers(token: String, organisation: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getOrganisationMembers(token, organisation, page)
                    .let { organisationMembers ->
                        if (organisationMembers.isSuccessful) {
                            _state.update {
                                it.copy(OrganisationMembers = Resource.Success(organisationMembers.body()!!))
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    OrganisationMembers = Resource.Failure(
                                        organisationMembers.errorBody().toString()
                                    )
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(OrganisationMembers = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getOrganisationRepositories(token: String, organisation: String, type: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getOrganisationRepos(token, organisation, page, type)
                    .let { organisationRepos ->
                        if (organisationRepos.isSuccessful) {
                            _state.update {
                                it.copy(OrganisationRepos = Resource.Success(organisationRepos.body()!!))
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    OrganisationRepos = Resource.Failure(
                                        organisationRepos.errorBody().toString()
                                    )
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(OrganisationRepos = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getOrg(token: String, organisation: String){
        viewModelScope.launch {
            try {
                repository.getOrganisation(token, organisation).let { org ->
                    if (org.isSuccessful){
                        _state.update {
                            it.copy(
                                Organisation = Resource.Success(org.body()!!)
                            )
                        }
                    }else{
                        _state.update {
                            it.copy(
                                Organisation = Resource.Failure(org.errorBody().toString())
                            )
                        }
                    }
                }
            }catch (e: Exception){
                _state.update {
                    it.copy(
                        Organisation = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }
}

data class OrganisationScreenState(
    val OrganisationMembers: Resource<OrganisationMemberModel> = Resource.Loading(),
    val OrganisationRepos: Resource<OrganisationsRepositoryModel> = Resource.Loading(),
    val Organisation: Resource<OrganisationModel> = Resource.Loading()
)