package com.hasan.jetfasthub.utility

object Constants {

    const val PERSONAL_ACCESS_TOKEN = "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"

    const val JetHubOwner = "HasanAnorov"
    const val JetHubRepoName = "JetHub"

    const val BASE_URL = "https://api.github.com/"
    const val BASIC_AUTH_URL = "https://github.com/"
    const val CLIENT_ID = "a84294a23f9142b73d0f"
    const val CLIENT_SECRET = "5547636a43ab2ee0688d751427397cb70bde06e9"
    const val REDIRECT_URL = "jetfasthub://login"
    const val STATE = "JetHub"
    const val SCOPE = "user,repo,gist,notifications,read:org"

    const val SHARED_PREF = "jetfasthub_pref"
    const val TOKEN_KEY = "jethub_pref_key"
    const val USERNAME_KEY = "jethub_pref_username"

    fun chooseFromEvents(type: String): EventsType {
        return when (type) {
            "WatchEvent" -> {
                EventsType.WatchEvent
            }

            "CreateEvent" -> {
                EventsType.CreateEvent
            }

            "CommitCommentEvent" -> {
                EventsType.CommitCommentEvent
            }

            "DownloadEvent" -> {
                EventsType.DownloadEvent
            }

            "FollowEvent" -> {
                EventsType.FollowEvent
            }

            "ForkEvent" -> {
                EventsType.ForkEvent
            }

            "GistEvent" -> {
                EventsType.GistEvent
            }

            "GollumEvent" -> {
                EventsType.GollumEvent
            }

            "IssueCommentEvent" -> {
                EventsType.IssueCommentEvent
            }

            "IssuesEvent" -> {
                EventsType.IssuesEvent
            }

            "MemberEvent" -> {
                EventsType.MemberEvent
            }

            "PublicEvent" -> {
                EventsType.PublicEvent
            }

            "PullRequestEvent" -> {
                EventsType.PullRequestEvent
            }

            "PullRequestReviewCommentEvent" -> {
                EventsType.PullRequestReviewCommentEvent
            }

            "PullRequestReviewEvent" -> {
                EventsType.PullRequestReviewEvent
            }

            "RepositoryEvent" -> {
                EventsType.RepositoryEvent
            }

            "PushEvent" -> {
                EventsType.PushEvent
            }

            "TeamAddEvent" -> {
                EventsType.TeamAddEvent
            }

            "DeleteEvent" -> {
                EventsType.DeleteEvent
            }

            "ReleaseEvent" -> {
                EventsType.ReleaseEvent
            }

            "ForkApplyEvent" -> {
                EventsType.ForkApplyEvent
            }

            "OrgBlockEvent" -> {
                EventsType.OrgBlockEvent
            }

            "ProjectCardEvent" -> {
                EventsType.ProjectCardEvent
            }

            "ProjectColumnEvent" -> {
                EventsType.ProjectColumnEvent
            }

            "OrganizationEvent" -> {
                EventsType.OrganizationEvent
            }

            "ProjectEvent" -> {
                EventsType.ProjectEvent
            }

            else -> {
                EventsType.Undefined
            }
        }
    }

}
