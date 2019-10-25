import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import java.util.function.Consumer

data class SearchApiResponse(
    val items: List<GithubRepo>
)

data class GithubRepo(
    @Json(name = "full_name") val name: String,
    @Json(name = "html_url") val url: String,
    val description: String?,
    val owner: Owner
)

data class Owner(
    @Json(name = "avatar_url") val avatarUrl: String?
)

object GithubApi {

    private const val GITHUB_ENDPOINT = "https://api.github.com"
    private const val SEARCH_REPOS_PATH = "/search/repositories"

    private val client by lazy { OkHttpClient() }
    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    fun search(searchTerm: String, searchCallback: (List<GithubRepo>) -> Unit) {
        search(searchTerm, Consumer { searchCallback(it) })
    }

    @JvmStatic
    fun search(searchTerm: String, searchCallback: Consumer<List<GithubRepo>>) {
        val searchReposRequest = Request.Builder()
            .url("$GITHUB_ENDPOINT$SEARCH_REPOS_PATH?q=$searchTerm")
            .build()

        client.newCall(searchReposRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson: String = response.body?.string() ?: return
                val searchResponseAdapter = moshi.adapter<SearchApiResponse>(SearchApiResponse::class.java)
                val searchApiResponseModel = searchResponseAdapter.fromJson(responseJson) ?: return

                searchCallback.accept(searchApiResponseModel.items)
            }
        })
    }
}
