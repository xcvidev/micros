package com.xcvi.micros.data.entity

import android.content.Context
import com.xcvi.micros.R
import kotlinx.serialization.Serializable

@Serializable
data class AminoAcids(
    var alanine: Double = 0.0,
    var arginine: Double = 0.0,
    var asparagine: Double = 0.0,
    var asparticAcid: Double = 0.0,
    var cystine: Double = 0.0,

    var glutamicAcid: Double = 0.0,
    var glutamine: Double = 0.0,
    var glycine: Double = 0.0,
    var histidine: Double = 0.0,
    var isoleucine: Double = 0.0,

    var leucine: Double = 0.0,
    var lysine: Double = 0.0,
    var methionine: Double = 0.0,
    var phenylalanine: Double = 0.0,
    var proline: Double = 0.0,

    var serine: Double = 0.0,
    var threonine: Double = 0.0,
    var tryptophan: Double = 0.0,
    var tyrosine: Double = 0.0,
    var valine: Double = 0.0
)

fun AminoAcids.toLabeledPairs(context: Context): List<Pair<String, String>> {
    return listOf(
        context.getString(R.string.alanine) to "$alanine g",
        context.getString(R.string.arginine) to "$arginine g",
        context.getString(R.string.asparagine) to "$asparagine g",
        context.getString(R.string.aspartic_acid) to "$asparticAcid g",
        context.getString(R.string.cystine) to "$cystine g",

        context.getString(R.string.glutamic_acid) to "$glutamicAcid g",
        context.getString(R.string.glutamine) to "$glutamine g",
        context.getString(R.string.glycine) to "$glycine g",
        context.getString(R.string.histidine) to "$histidine g",
        context.getString(R.string.isoleucine) to "$isoleucine g",

        context.getString(R.string.leucine) to "$leucine g",
        context.getString(R.string.lysine) to "$lysine g",
        context.getString(R.string.methionine) to "$methionine g",
        context.getString(R.string.phenylalanine) to "$phenylalanine g",
        context.getString(R.string.proline) to "$proline g",

        context.getString(R.string.serine) to "$serine g",
        context.getString(R.string.threonine) to "$threonine g",
        context.getString(R.string.tryptophan) to "$tryptophan g",
        context.getString(R.string.tyrosine) to "$tyrosine g",
        context.getString(R.string.valine) to "$valine g"
    )
}
