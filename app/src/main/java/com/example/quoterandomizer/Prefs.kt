package com.example.quoterandomizer

import android.content.Context
import org.json.JSONArray

object Prefs {
    private const val FILE = "quotes"
    private const val KEY_QUOTES = "list"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_MIN = "min"
    private const val KEY_MAX = "max"

    fun loadQuotes(c: Context): List<String> {
        val s = c.getSharedPreferences(FILE, 0).getString(KEY_QUOTES, "[]") ?: "[]"
        val a = JSONArray(s)
        return List(a.length()) { a.getString(it) }
    }
    fun saveQuotes(c: Context, q: List<String>) {
        val a = JSONArray()
        q.forEach { a.put(it) }
        c.getSharedPreferences(FILE, 0).edit().putString(KEY_QUOTES, a.toString()).apply()
    }
    fun enabled(c: Context) = c.getSharedPreferences(FILE, 0).getBoolean(KEY_ENABLED, false)
    fun setEnabled(c: Context, v: Boolean) { c.getSharedPreferences(FILE, 0).edit().putBoolean(KEY_ENABLED, v).apply() }
    fun minMinutes(c: Context) = c.getSharedPreferences(FILE, 0).getLong(KEY_MIN, 60)
    fun maxMinutes(c: Context) = c.getSharedPreferences(FILE, 0).getLong(KEY_MAX, 180)
}
