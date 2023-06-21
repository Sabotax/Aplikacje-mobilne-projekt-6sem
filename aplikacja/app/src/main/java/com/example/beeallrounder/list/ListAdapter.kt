package com.example.beeallrounder.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.beeallrounder.R
import com.example.beeallrounder.databases.oldDB.model.Beehive_snapshot
import com.example.beeallrounder.fragments.DBOld.DBListFragmentDirections

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var snapshots_list = emptyList<Beehive_snapshot>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = snapshots_list[position]
        holder.itemView.findViewById<TextView>(R.id.recyclerRowIDText).text = currentItem.id.toString()
        holder.itemView.findViewById<TextView>(R.id.recyclerRowDateText).text = currentItem.date
        holder.itemView.findViewById<TextView>(R.id.recyclerRowNumberText).text = currentItem.hiveNumber.toString()
        holder.itemView.findViewById<TextView>(R.id.recyclerRowNotesText).text = currentItem.notes

        holder.itemView.findViewById<ConstraintLayout>(R.id.rowLayout).setOnClickListener {
            val action = DBListFragmentDirections.actionDBListFragmentToDBUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(snap : List<Beehive_snapshot>) {
        this.snapshots_list = snap
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return snapshots_list.size
    }
}