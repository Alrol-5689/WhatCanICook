package com.app.ui.recipes.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.databinding.ItemStepBinding

class StepAdapter : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    val steps = mutableListOf<String>()

    init {
        steps.add("")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = steps.size

    fun addStep() {
        steps.add("")
        notifyItemInserted(steps.size - 1)
    }

    fun setSteps(newSteps: List<String>) {
        steps.clear()
        if (newSteps.isEmpty()) {
            steps.add("")
        } else {
            steps.addAll(newSteps)
        }
        notifyDataSetChanged()
    }

    inner class StepViewHolder(private val binding: ItemStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var textWatcher: TextWatcher? = null

        fun bind(position: Int) {
            if (textWatcher != null) {
                binding.etStepDescription.removeTextChangedListener(textWatcher)
            }

            binding.tvStepNumber.text = "${position + 1}."
            binding.etStepDescription.setText(steps[position])

            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        steps[bindingAdapterPosition] = s.toString()
                    }
                }
            }
            binding.etStepDescription.addTextChangedListener(textWatcher)

            binding.btnRemoveStep.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val actualPos = bindingAdapterPosition
                    steps.removeAt(actualPos)
                    notifyItemRemoved(actualPos)
                    notifyItemRangeChanged(actualPos, steps.size)
                }
            }
        }
    }
}

