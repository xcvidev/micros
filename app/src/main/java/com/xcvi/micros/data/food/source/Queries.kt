package com.xcvi.micros.data.food.source



const val getRecents =
    """
    SELECT *
    FROM portions
    WHERE isFavorite = 1
    OR (
        (barcode, date) IN (
            SELECT barcode, MAX(date)
            FROM portions
            GROUP BY barcode
        )
    )
    ORDER BY date
    LIMIT 30;
"""

const val sumMacrosByDate =
    """
        SELECT 
            SUM(macro_calories) AS calories,
            SUM(macro_protein) AS protein,
            SUM(macro_carbohydrates) AS carbohydrates,
            SUM(macro_fats) AS fats,
            SUM(macro_saturatedFats) AS saturatedFats,
            SUM(macro_fiber) AS fiber,
            SUM(macro_sugars) AS sugars,
            SUM(macro_salt) AS salt
        FROM portions
        WHERE date = :date
    """

const val sumMacrosByDateAndMeal =
    """
        SELECT 
            SUM(macro_calories) AS calories,
            SUM(macro_protein) AS protein,
            SUM(macro_carbohydrates) AS carbohydrates,
            SUM(macro_fats) AS fats,
            SUM(macro_saturatedFats) AS saturatedFats,
            SUM(macro_fiber) AS fiber,
            SUM(macro_sugars) AS sugars,
            SUM(macro_salt) AS salt
        FROM portions
        WHERE date = :date AND meal = :meal
    """
const val sumMineralsByDate =
    """
        SELECT 
            SUM(mineral_calcium) AS calcium,
            SUM(mineral_copper) AS copper,
            SUM(mineral_fluoride) AS fluoride,
            SUM(mineral_iron) AS iron,
            SUM(mineral_magnesium) AS magnesium,
            SUM(mineral_manganese) AS manganese,
            SUM(mineral_phosphorus) AS phosphorus,
            SUM(mineral_potassium) AS potassium,
            SUM(mineral_selenium) AS selenium,
            SUM(mineral_sodium) AS sodium,
            SUM(mineral_zinc) AS zinc
        FROM portions
        WHERE date = :date
    """
const val sumMineralsByDateAndMeal =
    """
        SELECT 
            SUM(mineral_calcium) AS calcium,
            SUM(mineral_copper) AS copper,
            SUM(mineral_fluoride) AS fluoride,
            SUM(mineral_iron) AS iron,
            SUM(mineral_magnesium) AS magnesium,
            SUM(mineral_manganese) AS manganese,
            SUM(mineral_phosphorus) AS phosphorus,
            SUM(mineral_potassium) AS potassium,
            SUM(mineral_selenium) AS selenium,
            SUM(mineral_sodium) AS sodium,
            SUM(mineral_zinc) AS zinc
        FROM portions
        WHERE date = :date AND meal = :meal
    """
const val sumVitaminsByDate =
    """
        SELECT 
            SUM(vitamin_vitaminA) AS vitaminA,
            SUM(vitamin_vitaminB1) AS vitaminB1,
            SUM(vitamin_vitaminB2) AS vitaminB2,
            SUM(vitamin_vitaminB3) AS vitaminB3,
            SUM(vitamin_vitaminB4) AS vitaminB4,
            SUM(vitamin_vitaminB5) AS vitaminB5,
            SUM(vitamin_vitaminB6) AS vitaminB6,
            SUM(vitamin_vitaminB9) AS vitaminB9,
            SUM(vitamin_vitaminB12) AS vitaminB12,
            SUM(vitamin_vitaminC) AS vitaminC,
            SUM(vitamin_vitaminD) AS vitaminD,
            SUM(vitamin_vitaminE) AS vitaminE,
            SUM(vitamin_vitaminK) AS vitaminK
        FROM portions
        WHERE date = :date
    """
const val sumVitaminsByDateAndMeal =
    """
        SELECT 
            SUM(vitamin_vitaminA) AS vitaminA,
            SUM(vitamin_vitaminB1) AS vitaminB1,
            SUM(vitamin_vitaminB2) AS vitaminB2,
            SUM(vitamin_vitaminB3) AS vitaminB3,
            SUM(vitamin_vitaminB4) AS vitaminB4,
            SUM(vitamin_vitaminB5) AS vitaminB5,
            SUM(vitamin_vitaminB6) AS vitaminB6,
            SUM(vitamin_vitaminB9) AS vitaminB9,
            SUM(vitamin_vitaminB12) AS vitaminB12,
            SUM(vitamin_vitaminC) AS vitaminC,
            SUM(vitamin_vitaminD) AS vitaminD,
            SUM(vitamin_vitaminE) AS vitaminE,
            SUM(vitamin_vitaminK) AS vitaminK
        FROM portions
        WHERE date = :date AND meal = :meal
    """
const val sumAminoacidsByDate =
    """
        SELECT 
            SUM(amino_alanine) AS alanine,
            SUM(amino_arginine) AS arginine,
            SUM(amino_asparagine) AS asparagine,
            SUM(amino_asparticAcid) AS asparticAcid,
            SUM(amino_cystine) AS cystine,
            SUM(amino_glutamicAcid) AS glutamicAcid,
            SUM(amino_glutamine) AS glutamine,
            SUM(amino_glycine) AS glycine,
            SUM(amino_histidine) AS histidine,
            SUM(amino_isoleucine) AS isoleucine,
            SUM(amino_leucine) AS leucine,
            SUM(amino_lysine) AS lysine,
            SUM(amino_methionine) AS methionine,
            SUM(amino_phenylalanine) AS phenylalanine,
            SUM(amino_proline) AS proline,
            SUM(amino_serine) AS serine,
            SUM(amino_threonine) AS threonine,
            SUM(amino_tryptophan) AS tryptophan,
            SUM(amino_tyrosine) AS tyrosine,
            SUM(amino_valine) AS valine
        FROM portions
        WHERE date = :date
    """
const val sumAminoacidsByDateAndMeal =
    """
        SELECT 
            SUM(amino_alanine) AS alanine,
            SUM(amino_arginine) AS arginine,
            SUM(amino_asparagine) AS asparagine,
            SUM(amino_asparticAcid) AS asparticAcid,
            SUM(amino_cystine) AS cystine,
            SUM(amino_glutamicAcid) AS glutamicAcid,
            SUM(amino_glutamine) AS glutamine,
            SUM(amino_glycine) AS glycine,
            SUM(amino_histidine) AS histidine,
            SUM(amino_isoleucine) AS isoleucine,
            SUM(amino_leucine) AS leucine,
            SUM(amino_lysine) AS lysine,
            SUM(amino_methionine) AS methionine,
            SUM(amino_phenylalanine) AS phenylalanine,
            SUM(amino_proline) AS proline,
            SUM(amino_serine) AS serine,
            SUM(amino_threonine) AS threonine,
            SUM(amino_tryptophan) AS tryptophan,
            SUM(amino_tyrosine) AS tyrosine,
            SUM(amino_valine) AS valine
        FROM portions
        WHERE date = :date AND meal = :meal
    """
