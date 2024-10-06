package com.swuproject.pawprints.ui.matching

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.network.SimilarSighting

class MatchingResultActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SimilarSightingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_result)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // 뒤로 가기 아이콘에 클릭 리스너 설정
        findViewById<ImageView>(R.id.icon_back).setOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.matchresult_matchReclyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SimilarSightingAdapter(this)
        recyclerView.adapter = adapter

        val similarSightings = intent.getSerializableExtra("similarSightings") as? List<SimilarSighting>
        similarSightings?.let {
            adapter.submitList(it)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 뒤로 가기 버튼을 눌렀을 때 SignUpActivity 종료
        finish()
    }
}
