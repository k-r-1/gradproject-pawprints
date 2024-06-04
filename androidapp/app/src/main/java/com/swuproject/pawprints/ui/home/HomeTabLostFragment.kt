package com.swuproject.pawprints.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentHomeTabLostBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeTabLostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeTabLostFragment : Fragment() {
    lateinit var binding: FragmentHomeTabLostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트합니다.
        val rootView = inflater.inflate(R.layout.fragment_home_tab_lost, container, false)

        // RecyclerView를 레이아웃에서 찾습니다.
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.hometab_lostRecyclerview)

        // LayoutManager를 설정합니다.
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        //drawable 폴더의 파일을 Drawable 형식으로 변환
        val photo1: Drawable? = context?.resources?.getDrawable(R.drawable.dog_sample, null)

        // RecyclerView에 표시할 데이터를 생성합니다.
        val list = ArrayList<LostRecyclerData>()
        list.add(LostRecyclerData(photo1, "title1", "dog", "w", "nowon", "20240426", "비선아파트", "빨간 목줄", "이슈니")) // Drawable 요소가 null이 됩니다.
        list.add(LostRecyclerData(photo1, "title2", "dog", "w", "nowon", "20240426", "비선아파트", "빨간 목줄", "이슈니")) // Drawable 요소가 null이 됩니다.
        list.add(LostRecyclerData(photo1, "title3", "dog", "w", "nowon", "20240426", "비선아파트", "빨간 목줄", "이슈니"))
        list.add(LostRecyclerData(photo1, "title4", "dog", "w", "nowon", "20240426", "비선아파트", "빨간 목줄", "이슈니"))
        // 어댑터를 생성하고 RecyclerView에 설정합니다.
        val adapter = LostRecyclerAdapter(list)
        recyclerView.adapter = adapter

        return rootView
    }
}