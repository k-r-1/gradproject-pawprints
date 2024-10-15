package com.swuproject.pawprints.ui.poster

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentPosterBinding
import com.swuproject.pawprints.dto.LostReportResponse
import com.swuproject.pawprints.network.Pet
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PosterFragment : Fragment() {

    private var _binding: FragmentPosterBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofitService: RetrofitService
    private var selectedPetName: String? = null // 선택된 반려동물 이름 저장
    private var selectedTextView: TextView? = null // 선택된 텍스트뷰 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        retrofitService = RetrofitClient.getRetrofitService()

        // SharedPreferences에서 사용자 ID 가져오기
        val userId = Utils.getUserId(requireContext())

        // 반려동물 목록 가져오기
        if (userId != null) {
            binding.petListSection.removeAllViews()
            val loadingTextView = TextView(requireContext()).apply {
                text = "반려동물 정보를 가져오는 중입니다..."
                textSize = 16f
            }
            binding.petListSection.addView(loadingTextView)
            fetchLostPets(userId) // 실종된 반려동물만 가져오기
        }

        // 포스터 생성 버튼 클릭 이벤트 설정
        binding.createPosterButton.setOnClickListener {
            if (userId != null && selectedPetName != null) {
                binding.progressBar.visibility = View.VISIBLE // 로딩 시작

                // 팝업 창을 띄워서 미리보기 화면을 보여주는 메소드 호출
                showPreviewPopup()

                // 미리보기 팝업이 뜬 후 ProgressBar를 멈춤
                binding.progressBar.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), "반려동물을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    // 사용자 ID로 실종된 반려동물 목록 가져오기
    private fun fetchLostPets(userId: String) {
        retrofitService.getLostPetsByUserId(userId).enqueue(object : Callback<List<Pet>> {
            override fun onResponse(call: Call<List<Pet>>, response: Response<List<Pet>>) {
                if (response.isSuccessful) {
                    response.body()?.let { pets ->
                        displayLostPets(pets)
                    }
                } else {
                    showError("반려동물 정보를 가져오는 데 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<List<Pet>>, t: Throwable) {
                showError("반려동물 정보를 가져오는 데 실패했습니다.")
            }
        })
    }

    // 실종된 반려동물 목록을 화면에 표시
    private fun displayLostPets(pets: List<Pet>) {
        binding.petListSection.removeAllViews() // 기존에 추가된 뷰들을 모두 제거

        if (pets.isNotEmpty()) { // 반려동물 목록이 비어있지 않은 경우
            for (pet in pets) {
                val petName = TextView(requireContext()).apply {
                    text = pet.name
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    setPadding(16, 16, 16, 16)
                    setOnClickListener {
                        // 이미 선택된 반려동물을 다시 클릭하면 선택 해제
                        if (selectedPetName == pet.name) {
                            selectedPetName = null
                            setTextColor(Color.BLACK)
                            selectedTextView = null
                            // 실종 신고 정보 초기화
                            clearLostReportInfo()
                            binding.createPosterButton.visibility = View.GONE // 매칭 버튼 숨기기
                        } else {
                            selectedPetName = pet.name
                            setTextColor(resources.getColor(R.color.deep_pink))
                            selectedTextView?.setTextColor(Color.BLACK) // 이전 선택 해제
                            selectedTextView = this
                            fetchLostReport(pet.id)
                            binding.createPosterButton.visibility = View.VISIBLE // 매칭 버튼 보이기
                        }
                    }
                }
                binding.petListSection.addView(petName)
            }
        } else { // 반려동물 목록이 비어있는 경우
            showError("실종된 반려동물 정보가 없습니다.")
        }
    }

    // 실종 신고 정보 초기화 메서드 추가
    private fun clearLostReportInfo() {
        binding.petImage.setImageResource(R.drawable.dog_sample2) // 기본 이미지 설정
        binding.posterSection.visibility = View.GONE // 실종 신고 섹션 숨기기
        binding.lostReportTitle.setText("")
        binding.lostReportBreed.setText("")
        binding.lostReportGender.setText("")
        binding.lostReportAge.setText("")
        binding.lostReportArea.setText("")
        binding.lostReportDate.setText("")
        binding.lostReportFeature.setText("")
        binding.lostReportLocation.setText("")
        binding.lostReportDescription.setText("")
        binding.lostReportContact.setText("")
    }


    // 오류 메시지 표시
    private fun showError(message: String) {
        binding.petListSection.removeAllViews()
        val errorTextView = TextView(requireContext()).apply {
            text = message
            textSize = 16f
        }
        binding.petListSection.addView(errorTextView)
    }

    // 반려동물 ID로 실종 신고 정보 가져오기
    private fun fetchLostReport(petId: Int) {
        retrofitService.getLostReportByPetId(petId).enqueue(object : Callback<LostReportResponse> { // 여기를 LostReportResponse로 변경
            override fun onResponse(call: Call<LostReportResponse>, response: Response<LostReportResponse>) { // 여기도 변경
                if (response.isSuccessful) {
                    response.body()?.let { lostReport ->
                        displayLostReport(lostReport)
                    }
                } else {
                    showErrorToast("실종 신고 정보를 가져오는 데 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<LostReportResponse>, t: Throwable) { // 여기도 변경
                showErrorToast("실종 신고 정보를 가져오는 데 실패했습니다.")
            }
        })
    }

    // 토스트 메시지로 오류 표시
    private fun showErrorToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // 실종 신고 정보를 화면에 표시
    private fun displayLostReport(lostReportResponse: LostReportResponse) {
        val firstImage = lostReportResponse.lostImages.firstOrNull()?.lostImagePath
        if (firstImage != null) {
            // gs:// URL을 https://storage.googleapis.com/로 변환
            val imageUrl = firstImage.replace("gs://", "https://storage.googleapis.com/")
            // Glide를 사용해 이미지 로드
            Glide.with(requireContext()).load(imageUrl).into(binding.petImage)
        } else {
            binding.petImage.setImageResource(R.drawable.dog_sample2) // 기본 이미지
        }
        binding.posterSection.visibility = View.VISIBLE
        binding.lostReportTitle.setText(lostReportResponse.lostTitle) // EditText는 setText() 사용
        binding.lostReportBreed.setText(lostReportResponse.petBreed) // EditText는 setText() 사용
        binding.lostReportGender.setText(lostReportResponse.petGender) // EditText는 setText() 사용
        // 나이에 "살" 추가
        binding.lostReportAge.setText("${lostReportResponse.petAge}살")

        // Nullable 타입을 처리하여 주소 변환
        val latitude = lostReportResponse.lostAreaLat ?: 0.0
        val longitude = lostReportResponse.lostAreaLng ?: 0.0

        CoroutineScope(Dispatchers.Main).launch {
            val address = withContext(Dispatchers.IO) {
                getAddressFromLatLng(latitude, longitude)
            }
            binding.lostReportArea.setText(address ?: "$latitude, $longitude")
        }

        // 날짜 포맷 변경
        binding.lostReportDate.setText(formatDate(lostReportResponse.lostDate)) // EditText는 setText() 사용

        binding.lostReportFeature.setText(lostReportResponse.petFeature) // EditText는 setText() 사용
        binding.lostReportLocation.setText(lostReportResponse.lostLocation) // EditText는 setText() 사용
        binding.lostReportDescription.setText(lostReportResponse.lostDescription) // EditText는 setText() 사용
        binding.lostReportContact.setText(lostReportResponse.lostContact) // EditText는 setText() 사용
    }

    // 날짜 포맷 변경 함수
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""

        // 받은 날짜 형식
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        // 원하는 출력 형식
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        return try {
            val date: Date = inputFormat.parse(dateString) ?: return dateString
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            dateString // 변환 실패 시 원본 문자열 반환
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) // 전체 주소 반환
            } else {
                null // 주소를 찾지 못한 경우
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // 예외 발생 시 null 반환
        }
    }

    private fun showPreviewPopup() {
        // 팝업 레이아웃을 정의하고 inflate
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.preview_popup, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        // 닫기 버튼 처리
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)
        closeButton.setOnClickListener {
            alertDialog.dismiss() // 팝업 닫기
        }

        // posterSection을 Bitmap으로 변환
        val bitmap = getBitmapFromView(binding.posterSection) // posterSection을 사용

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


    // View를 Bitmap으로 변환하는 함수
    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    private fun showSaveOptions(bitmap: Bitmap) {
        val saveOptions = arrayOf("JPG", "PNG", "PDF")
        val builder = AlertDialog.Builder(requireContext())
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
        // A4 크기를 픽셀 단위로 변환 (300 DPI 기준)
        val a4WidthPx = 2480 // A4 너비 (픽셀)
        val a4HeightPx = 3508 // A4 높이 (픽셀)

        // View의 실제 크기와 비율을 계산하여 A4 크기로 스케일 조정
        val scaleFactor = a4WidthPx.toFloat() / view.width
        val newHeight = (view.height * scaleFactor).toInt()

        // 비율에 맞게 A4 크기로 Bitmap 생성
        val a4Bitmap = Bitmap.createBitmap(a4WidthPx, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(a4Bitmap)
        canvas.scale(scaleFactor, scaleFactor)
        view.draw(canvas)
        return a4Bitmap
    }

    // JPG로 저장
    private fun saveAsJpg(bitmap: Bitmap) {
        val filename = "poster_${System.currentTimeMillis()}.jpg"
        saveImage(getA4SizeBitmap(binding.posterSection), filename, Bitmap.CompressFormat.JPEG)
    }

    // PNG로 저장
    private fun saveAsPng(bitmap: Bitmap) {
        val filename = "poster_${System.currentTimeMillis()}.png"
        saveImage(getA4SizeBitmap(binding.posterSection), filename, Bitmap.CompressFormat.PNG)
    }


    // PDF로 저장
    private fun saveAsPdf(bitmap: Bitmap) {
        val document = PdfDocument()

        // A4 크기 설정 (픽셀 단위로 설정, 300 DPI 기준)
        val a4WidthPx = 2480 // A4 너비
        val a4HeightPx = 3508 // A4 높이

        // 포스터 섹션의 비율 유지하면서 A4에 맞게 크기 조정
        val scaledBitmap = getA4FittedBitmap(bitmap, a4WidthPx, a4HeightPx)

        // 페이지 정보 설정
        val pageInfo = PdfDocument.PageInfo.Builder(a4WidthPx, a4HeightPx, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // 비율에 맞게 이미지를 A4 크기 내에서 중앙에 배치
        val leftMargin = (a4WidthPx - scaledBitmap.width) / 2f  // 수평 가운데 정렬
        val topMargin = (a4HeightPx - scaledBitmap.height) / 2f // 수직 가운데 정렬
        canvas.drawBitmap(scaledBitmap, leftMargin, topMargin, null)

        document.finishPage(page)

        val filename = "poster_${System.currentTimeMillis()}.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        try {
            document.writeTo(FileOutputStream(file))
            Toast.makeText(requireContext(), "PDF로 저장되었습니다. ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "PDF 저장에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        document.close()
    }

    // A4 크기에 맞게 비율을 유지하면서 이미지를 조정하는 함수
    private fun getA4FittedBitmap(originalBitmap: Bitmap, a4WidthPx: Int, a4HeightPx: Int): Bitmap {
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height

        // A4 크기 내에서 비율을 유지하면서 축소
        val widthRatio = a4WidthPx.toFloat() / originalWidth
        val heightRatio = a4HeightPx.toFloat() / originalHeight
        val scaleFactor = minOf(widthRatio, heightRatio) // 가로와 세로 비율 중 더 작은 값으로 맞춤

        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()

        // 비율에 맞게 조정된 새 Bitmap 생성
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
    }


    // 이미지 파일 저장 공통 함수
    // A4 크기에서 이미지 파일 저장
    private fun saveImage(bitmap: Bitmap, filename: String, format: Bitmap.CompressFormat) {
        // 다운로드 폴더에 저장
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(format, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(requireContext(), "이미지가 저장되었습니다. ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "이미지 저장에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showShareOptions(bitmap: Bitmap) {
        val file = saveBitmapToCache(bitmap) // 공유할 비트맵을 캐시에 저장
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "공유하기"))
    }

    // Bitmap을 임시로 캐시에 저장하는 함수
    private fun saveBitmapToCache(bitmap: Bitmap): File {
        val cachePath = File(requireContext().cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_image.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
