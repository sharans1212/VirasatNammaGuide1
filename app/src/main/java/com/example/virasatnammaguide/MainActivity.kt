package com.example.virasatnammaguide

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var siteContainer: LinearLayout
    private val checkInDao by lazy { AppDatabase.get(this).checkInDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        siteContainer = findViewById(R.id.siteContainer)
        findViewById<Button>(R.id.scanButton).setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }
        findViewById<Button>(R.id.passportButton).setOnClickListener {
            showPassport()
        }
        findViewById<Button>(R.id.aboutButton).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        renderSites()
    }

    private fun renderSites() {
        siteContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)

        HeritageRepository.sites.forEach { site ->
            val card = inflater.inflate(R.layout.site_card, siteContainer, false)
            card.findViewById<TextView>(R.id.siteName).text = site.nameEn
            card.findViewById<TextView>(R.id.siteDistance).text = String.format(Locale.US, "%.1f km away", site.distanceKm)
            card.findViewById<TextView>(R.id.siteSummary).text = site.summaryEn
            
            val imageView = card.findViewById<ImageView>(R.id.siteImage)
            imageView.setImageResource(site.imageResId)

            card.findViewById<Button>(R.id.openSiteButton).setOnClickListener {
                openSite(site.id)
            }
            siteContainer.addView(card)
        }
    }

    private fun openSite(siteId: String) {
        startActivity(
            Intent(this, SiteDetailActivity::class.java).putExtra(SiteDetailActivity.EXTRA_SITE_ID, siteId)
        )
    }

    private fun showPassport() {
        lifecycleScope.launch {
            val checkIns = withContext(Dispatchers.IO) { checkInDao.getAll() }
            val message = if (checkIns.isEmpty()) {
                getString(R.string.passport_empty)
            } else {
                val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                checkIns.joinToString(separator = "\n\n") { checkIn ->
                    val siteName = HeritageRepository.findById(checkIn.siteId)?.nameEn ?: checkIn.siteId
                    "$siteName\n${dateFormat.format(Date(checkIn.checkedInAt))}"
                }
            }

            AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.passport_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.close), null)
                .show()
        }
    }
}
