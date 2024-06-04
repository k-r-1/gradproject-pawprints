package com.swuproject.pawprints.ui.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentHomeBinding
import com.swuproject.pawprints.databinding.FragmentHomeTabSightBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeTabSightFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeTabSightFragment : Fragment() {
    lateinit var binding: FragmentHomeTabSightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트합니다.
        val rootView = inflater.inflate(R.layout.fragment_home_tab_sight, container, false)

        // RecyclerView를 레이아웃에서 찾습니다.
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.hometab_sightRecyclerview)

        // LayoutManager를 설정합니다.
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        //drawable 폴더의 파일을 Drawable 형식으로 변환
        val photo1: Drawable? = context?.resources?.getDrawable(R.drawable.dog_sample2, null)

        // RecyclerView에 표시할 데이터를 생성합니다.
        val list = ArrayList<SightRecyclerData>()
        list.add(SightRecyclerData(photo1, "title1", "dog", "영등포구 여의도동", "2024.04.26 15:30", "한강아파트", "파란 목줄")) // Drawable 요소가 null이 됩니다.
        list.add(SightRecyclerData(photo1, "title2", "dog", "영등포구 당산동", "2024.04.27 15:31", "현대아파트", "파란 목줄")) // Drawable 요소가 null이 됩니다.
        list.add(SightRecyclerData(photo1, "title3", "dog", "영등포구 문래동", "2024.04.28 15:32", "진로아파트", "파란 목줄"))
        list.add(SightRecyclerData(photo1, "title4", "dog", "영등포구 신길동", "2024.04.29 15:33", "신길아파트", "파란 목줄"))
        // 어댑터를 생성하고 RecyclerView에 설정합니다.
        val adapter = SightRecyclerAdapter(list)
        recyclerView.adapter = adapter

        return rootView
    }
}