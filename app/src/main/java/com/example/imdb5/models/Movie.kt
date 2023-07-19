package com.example.imdb5.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Movie(

    @SerializedName("backdrop_path")
    val backdrop: String?,   //or null

    val id: Int?, //int

    val title: String?,

    @SerializedName("original_title")
    val originalTitle: String?,

    val tagline : String?,  //frase post titulo

    @SerializedName("vote_average")
    val voteAverage: String?,   //number? float?

    @SerializedName("poster_path")
    val poster: String?,   //or null

    val overview: String?,   // or null   dejo o saco?

    val runtime: String?,  // int or null

    @SerializedName("original_language")
    var originalLanguage: String?,

    @SerializedName("release_date")
    val release: String?,

    @SerializedName("vote_count")
    val voteCount: String?

    ) : Parcelable {


    @JvmName("getPoster1") // q verga es esto
    fun getPoster(): String? {
        return poster
    }
    //getters



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

        if (backdrop != other.backdrop) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (voteAverage != other.voteAverage) return false
        if (poster != other.poster) return false
        if (overview != other.overview) return false
        if (runtime != other.runtime) return false
        if (release != other.release) return false

        return true
    }



    override fun hashCode(): Int {
        var result = backdrop?.hashCode() ?: 0
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (voteAverage?.hashCode() ?: 0)
        result = 31 * result + (poster?.hashCode() ?: 0)
        result = 31 * result + (overview?.hashCode() ?: 0)
        result = 31 * result + (runtime?.hashCode() ?: 0)
        result = 31 * result + (release?.hashCode() ?: 0)
        return result
    }
}



