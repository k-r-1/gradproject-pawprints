package com.swuproject.pawprints.ui.poster

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PosterManualActivity : AppCompatActivity() {

    private lateinit var posterSection: View // 포스터 섹션의 View를 참조
    private lateinit var petImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poster_manual)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        posterSection = findViewById(R.id.posterSection) // XML에서 posterSection을 찾아 설정
        petImage = findViewById(R.id.pet_image)

        // 예시로 이미지 로딩
        Glide.with(this).load(R.drawable.dog_sample2).into(petImage)

        // 포스터 생성 버튼
        findViewById<Button>(R.id.createPosterButton).setOnClickListener {
            showPreviewPopup()
        }

        // 뒤로 가기 아이콘에 클릭 리스너 설정
        findViewById<ImageView>(R.id.icon_back).setOnClickListener {
            onBackPressed()
        }
    }

    private fun showPreviewPopup() {
        // 팝업 레이아웃을 정의하고 inflate
        val dialogView = layoutInflater.inflate(R.layout.preview_popup, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        // 닫기 버튼 처리
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)
        closeButton.setOnClickListener {
            alertDialog.dismiss() // 팝업 닫기
        }

        // posterSection을 Bitmap으로 변환
        val bitmap = getBitmapFromView(posterSection)

        // 미리보기 이미지뷰에 Bitmap 적용
        val previewImageView = dialogView.findViewById<ImageView>(R.id.previewImageView)
        previewImageView.setImageBitmap(bitmap)

        // 저장 버튼 클릭 시 저장 다이얼로그 표시
        dialogView.findViewById<Button>(R.id.saveButton).setOnClickListener {
            showSaveOptions(bitmap)
        }

        // 공유 버튼 클릭 시 공유 다이얼로그 표시
        dialogView.findViewById<Button>(R.id.shareButton).setOnClickListener {
            showShareOptions(bitmap)
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun showSaveOptions(bitmap: Bitmap) {
        val saveOptions = arrayOf("JPG", "PNG", "PDF")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("저장 형식 선택")
            .setItems(saveOptions) { _, which ->
                when (which) {
                    0 -> saveAsJpg(bitmap)
                    1 -> saveAsPng(bitmap)
                    2 -> saveAsPdf(bitmap)
                }
            }
        builder.show()
    }

    private fun getA4SizeBitmap(view: View): Bitmap {
        val a4WidthPx = 2480
        val a4HeightPx = 3508
        val scaleFactor = a4WidthPx.toFloat() / view.width
        val newHeight = (view.height * scaleFactor).toInt()
        val a4Bitmap = Bitmap.createBitmap(a4WidthPx, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(a4Bitmap)
        canvas.scale(scaleFactor, scaleFactor)
        view.draw(canvas)
        return a4Bitmap
    }

    private fun saveAsJpg(bitmap: Bitmap) {
        val filename = "poster_${System.currentTimeMillis()}.jpg"
        saveImage(getA4SizeBitmap(posterSection), filename, Bitmap.CompressFormat.JPEG)
    }

    private fun saveAsPng(bitmap: Bitmap) {
        val filename = "poster_${System.currentTimeMillis()}.png"
        saveImage(getA4SizeBitmap(posterSection), filename, Bitmap.CompressFormat.PNG)
    }

    private fun saveAsPdf(bitmap: Bitmap) {
        val document = PdfDocument()

        val a4WidthPx = 2480
        val a4HeightPx = 3508
        val scaledBitmap = getA4FittedBitmap(bitmap, a4WidthPx, a4HeightPx)

        val pageInfo = PdfDocument.PageInfo.Builder(a4WidthPx, a4HeightPx, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val leftMargin = (a4WidthPx - scaledBitmap.width) / 2f
        val topMargin = (a4HeightPx - scaledBitmap.height) / 2f
        canvas.drawBitmap(scaledBitmap, leftMargin, topMargin, null)

        document.finishPage(page)

        val filename = "poster_${System.currentTimeMillis()}.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        try {
            document.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF로 저장되었습니다. ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "PDF 저장에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        document.close()
    }

    private fun getA4FittedBitmap(originalBitmap: Bitmap, a4WidthPx: Int, a4HeightPx: Int): Bitmap {
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height
        val widthRatio = a4WidthPx.toFloat() / originalWidth
        val heightRatio = a4HeightPx.toFloat() / originalHeight
        val scaleFactor = minOf(widthRatio, heightRatio)
        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
    }

    private fun saveImage(bitmap: Bitmap, filename: String, format: Bitmap.CompressFormat) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(format, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "이미지가 저장되었습니다. ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "이미지 저장에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showShareOptions(bitmap: Bitmap) {
        val file = saveBitmapToCache(bitmap)
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "공유하기"))
    }

    private fun saveBitmapToCache(bitmap: Bitmap): File {
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_image.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        return file
    }
}
