package com.example.virasatnammaguide

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class SiteDetailActivity : AppCompatActivity() {
    private lateinit var site: HeritageSite
    private lateinit var audioButton: Button
    private lateinit var checkInButton: Button
    private var mediaPlayer: MediaPlayer? = null
    private val checkInDao by lazy { AppDatabase.get(this).checkInDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_detail)

        val siteId = intent.getStringExtra(EXTRA_SITE_ID).orEmpty()
        site = HeritageRepository.findById(siteId) ?: run {
            Toast.makeText(this, getString(R.string.unknown_site), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        audioButton = findViewById(R.id.audioButton)
        checkInButton = findViewById(R.id.checkInButton)

        findViewById<TextView>(R.id.siteName).text = site.nameEn
        findViewById<TextView>(R.id.siteId).text = site.id

        findViewById<RadioGroup>(R.id.languageGroup).setOnCheckedChangeListener { _, checkedId ->
            bindSiteText(checkedId == R.id.kannadaOption)
        }

        audioButton.setOnClickListener { toggleAudio() }
        checkInButton.setOnClickListener { saveCheckIn() }

        bindSiteText(kannada = false)
        refreshCheckInState()
    }

    private fun bindSiteText(kannada: Boolean) {
        findViewById<TextView>(R.id.siteName).text = site.name(kannada)
        findViewById<TextView>(R.id.description).text = site.description(kannada)
        findViewById<TextView>(R.id.architecture).text = site.architecture(kannada)
        findViewById<TextView>(R.id.legend).text = site.legend(kannada)
        findViewById<TextView>(R.id.hiddenFact).text = site.hiddenFact(kannada)
    }

    private fun refreshCheckInState() {
        lifecycleScope.launch {
            val checkedIn = withContext(Dispatchers.IO) { checkInDao.getBySiteId(site.id) != null }
            checkInButton.text = if (checkedIn) getString(R.string.checked_in) else getString(R.string.check_in)
        }
    }

    private fun saveCheckIn() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                checkInDao.upsert(CheckInEntity(site.id, System.currentTimeMillis()))
            }
            checkInButton.text = getString(R.string.checked_in)
        }
    }

    private fun toggleAudio() {
        val player = mediaPlayer ?: createPlayer().also { mediaPlayer = it }
        if (player.isPlaying) {
            player.pause()
            audioButton.text = getString(R.string.play_audio)
        } else {
            player.start()
            audioButton.text = getString(R.string.pause_audio)
        }
    }

    private fun createPlayer(): MediaPlayer {
        return MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(createAudioGuideFile().absolutePath)
            prepare()
            setOnCompletionListener {
                seekTo(0)
                audioButton.text = getString(R.string.play_audio)
            }
        }
    }

    private fun createAudioGuideFile(): File {
        val file = File(cacheDir, "${site.id.lowercase()}_guide.wav")
        if (!file.exists()) {
            writeToneWav(file, frequencyHz = 420 + abs(site.id.hashCode() % 180))
        }
        return file
    }

    private fun writeToneWav(file: File, frequencyHz: Int) {
        val sampleRate = 16_000
        val seconds = 2
        val sampleCount = sampleRate * seconds
        val pcmData = ByteArray(sampleCount * 2)

        for (i in 0 until sampleCount) {
            val fade = when {
                i < sampleRate / 10 -> i / (sampleRate / 10.0)
                i > sampleCount - sampleRate / 10 -> (sampleCount - i) / (sampleRate / 10.0)
                else -> 1.0
            }
            val sample = (sin(2.0 * PI * frequencyHz * i / sampleRate) * Short.MAX_VALUE * 0.25 * fade).toInt().toShort()
            ByteBuffer.wrap(pcmData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(sample)
        }

        FileOutputStream(file).use { output ->
            val byteRate = sampleRate * 2
            val totalDataLen = pcmData.size + 36
            output.write("RIFF".toByteArray())
            output.write(intToBytes(totalDataLen))
            output.write("WAVEfmt ".toByteArray())
            output.write(intToBytes(16))
            output.write(shortToBytes(1))
            output.write(shortToBytes(1))
            output.write(intToBytes(sampleRate))
            output.write(intToBytes(byteRate))
            output.write(shortToBytes(2))
            output.write(shortToBytes(16))
            output.write("data".toByteArray())
            output.write(intToBytes(pcmData.size))
            output.write(pcmData)
        }
    }

    private fun intToBytes(value: Int): ByteArray = ByteBuffer.allocate(4)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putInt(value)
        .array()

    private fun shortToBytes(value: Int): ByteArray = ByteBuffer.allocate(2)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putShort(value.toShort())
        .array()

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    companion object {
        const val EXTRA_SITE_ID = "extra_site_id"
    }
}
