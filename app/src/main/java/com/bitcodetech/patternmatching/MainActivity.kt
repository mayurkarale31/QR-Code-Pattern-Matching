package com.bitcodetech.patternmatching

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.bitcodetech.patternmatching.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.util.EnumMap

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnQRScan.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setBarcodeImageEnabled(true)
            integrator.setCameraId(0)
            integrator.setPrompt("Scan The QR Code")
            integrator.setBeepEnabled(false)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result : IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result!=null) {

            if (result.contents != null) {

                binding.txtQRCodeValue.text = result.contents
                if (isValidQRCode(result.contents)) {
                    binding.txtQRCodeResult.setBackgroundColor(Color.GREEN)
                    binding.txtQRCodeResult.text = "OK"

                    val imgScannedQRCode : ImageView = binding.imgScannedQRCode
                    val txtQRCodeValue = result.contents
                    val bitmap = generateQRCode(txtQRCodeValue)

                    imgScannedQRCode.setImageBitmap(bitmap)

                }
                else {
                    binding.txtQRCodeResult.setBackgroundColor(Color.RED)
                    binding.txtQRCodeResult.text = "Not OK"

                    val imgScannedQRCode : ImageView = binding.imgScannedQRCode
                    val txtQRCodeValue = result.contents
                    val bitmap = generateQRCode(txtQRCodeValue)

                    imgScannedQRCode.setImageBitmap(bitmap)
                }
            }
            else{
                mt("Not Scanned")
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun isValidQRCode(QRCode : String) : Boolean{
        return QRCode.matches(Regex("[A-Z]{3}[0-9][A-Z][0-9]{5}"))

    }

    private fun generateQRCode(txtQRCodeResult: String) : Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val writer = MultiFormatWriter()
        val bitMatrix : BitMatrix = writer.encode(txtQRCodeResult, BarcodeFormat.QR_CODE, 300,300, hints)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix[x,y]) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, width, 0, 0,width, height)
        return bitmap
    }

    private fun mt(text : String) {
        Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
    }
}
