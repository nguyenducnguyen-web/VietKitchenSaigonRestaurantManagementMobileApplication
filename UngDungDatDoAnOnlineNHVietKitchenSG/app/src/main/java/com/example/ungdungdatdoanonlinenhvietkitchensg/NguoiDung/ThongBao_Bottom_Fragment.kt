package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentThongBaoBottomBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.ThongBaoAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.utils.NotificationManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThongBao_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentThongBaoBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThongBaoBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationList = NotificationManager.getNotifications()
        val notifications = notificationList.map { it.first }.toList()
        val notificationImages = notificationList.map { it.second }.toList()

        val adapter = ThongBaoAdapter(ArrayList(notifications), ArrayList(notificationImages))
        binding.notificatonRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificatonRecyclerView.adapter = adapter
    }
}