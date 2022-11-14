package com.boosters.promise

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.parseAsHtml
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.boosters.promise.databinding.ActivityAddPromiseBinding
import com.boosters.promise.network.LocalResponse
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.INPUT_MODE_CALENDAR
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddPromiseActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAddPromiseBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_add_promise)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        binding.buttonAddPromiseSelectDate.setOnClickListener(selectDateListener(calendar))
        binding.buttonAddPromiseSelectTime.setOnClickListener(selectTimeListener(calendar))
        binding.buttonAddPromiseSearchAddress.setOnClickListener(searchAddressListener())
    }

    private fun selectDateListener(cal: Calendar) = View.OnClickListener {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(cal.timeInMillis)
            .setInputMode(INPUT_MODE_CALENDAR)
            .setTitleText(getString(R.string.title_datepicker))
            .build()

        datePicker.show(supportFragmentManager, DATEPICKER_TAG)
        datePicker.addOnPositiveButtonClickListener {
            cal.timeInMillis = datePicker.selection ?: cal.timeInMillis
            binding.buttonAddPromiseSelectDate.text =
                getString(R.string.date_format).format(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
                )
        }
    }

    private fun selectTimeListener(cal: Calendar) = View.OnClickListener {
        val isSystem24Hour = is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(cal.get(Calendar.HOUR))
            .setMinute(cal.get(Calendar.MINUTE))
            .setInputMode(INPUT_MODE_CLOCK)
            .setTitleText(getString(R.string.title_timepicker))
            .build()

        timePicker.show(supportFragmentManager, TIMEPICKER_TAG)
        timePicker.addOnPositiveButtonClickListener {
            cal.set(Calendar.HOUR, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            binding.buttonAddPromiseSelectTime.text =
                getString(R.string.time_format).format(
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE)
                )
        }
    }

    private fun searchAddressListener() = View.OnClickListener {
        SearchAddressDialogFragment()
            .setOnSearchAddressDialogListener(object :
                SearchAddressDialogFragment.SearchAddressDialogListener {
                override fun onDialogPositiveClick(dialog: DialogFragment, result: LocalResponse) {
                    val items = result.items

                    if (items.isNotEmpty()) {
                        items.first()?.let { firstItem ->
                            binding.buttonAddPromiseSearchAddress.text =
                                getString(R.string.address_format).format(
                                    firstItem.title,
                                    firstItem.mapx,
                                    firstItem.mapy
                                ).parseAsHtml()
                        }
                    } else {
                        binding.buttonAddPromiseSearchAddress.text =
                            getString(R.string.search_address)
                    }
                }

                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    binding.buttonAddPromiseSearchAddress.text = getString(R.string.search_address)
                }
            })
            .show(supportFragmentManager, SEARCH_DIALOG_TAG)
    }

    companion object {
        const val DATEPICKER_TAG = "New Selected Date"
        const val TIMEPICKER_TAG = "New Selected Time"
        const val SEARCH_DIALOG_TAG = "New Search Address Dialog"
    }
}