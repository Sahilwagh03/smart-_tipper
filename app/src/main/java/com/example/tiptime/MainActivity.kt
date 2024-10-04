/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tiptime

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.tiptime.databinding.ActivityMainBinding
import java.text.NumberFormat

/**
 * Activity that displays a tip calculator.
 */
class MainActivity : AppCompatActivity() {

    // Binding object instance with access to the views in the activity_main.xml layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout XML file and return a binding object instance
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set the content view of the Activity to be the root view of the layout
        setContentView(binding.root)

        // Setup a click listener on the calculate button to calculate the tip
        binding.calculateButton.setOnClickListener { calculateTip() }

        // Set up a key listener on the EditText field to listen for "enter" button presses
        binding.costOfServiceEditText.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
    }

    /**
     * Calculates the tip based on the user input.
     */
    private fun calculateTip() {
        // Get the decimal value from the cost of service EditText field
        val stringInTextField = binding.costOfServiceEditText.text.toString()
        val cost = stringInTextField.toDoubleOrNull()

        // If the cost is null or 0, then display 0 tip and exit this function early.
        if (cost == null || cost == 0.0) {
            displayTipAndTotal(0.0, 0.0)
            return
        }

        // Get the tip percentage based on service quality selected
        val tipPercentage = when (binding.tipOptions.checkedRadioButtonId) {
            R.id.option_poor_service -> 0.10
            R.id.option_average_service -> 0.15
            R.id.option_good_service -> 0.18
            R.id.option_excellent_service -> 0.20
            else -> 0.15
        }

        // Calculate the tip
        var tip = tipPercentage * cost
        var totalBill = cost + tip

//        // If the switch for rounding up the tip toggled on (isChecked is true), then round up the
//        // tip. Otherwise do not change the tip value.
//        val roundUp = binding.roundUpSwitch.isChecked
//        if (roundUp) {
//            // Take the ceiling of the current tip, which rounds up to the next integer, and store
//            // the new value in the tip variable.
//            tip = kotlin.math.ceil(tip)
//        }

        // Handle the two switches: Round Tip and Round Total Bill
        when {
            binding.roundUpSwitch.isChecked -> {
                // Round the tip up if the "Round Tip" switch is enabled
                tip = kotlin.math.ceil(tip)
                totalBill = cost + tip
                binding.roundTotalSwitch.isChecked = false // Uncheck the other switch
            }
            binding.roundTotalSwitch.isChecked -> {
                // Round the total bill to the nearest whole number if the "Round Total Bill" switch is enabled
                totalBill = kotlin.math.ceil(totalBill)
                binding.roundUpSwitch.isChecked = false // Uncheck the other switch
            }
        }

        // Display the formatted tip value onscreen
        displayTipAndTotal(tip, totalBill)
    }

    /**
     * Format the tip amount according to the local currency and display it onscreen.
     * Example would be "Tip Amount: $10.00".
     */
    private fun displayTipAndTotal(tip: Double, totalBill: Double) {
        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        val formattedTotal = NumberFormat.getCurrencyInstance().format(totalBill)

        // Show both tip and total bill
        binding.tipResult.text = getString(R.string.tip_amount, formattedTip)
        binding.totalResult.text = getString(R.string.total_amount, formattedTotal)
    }

    /**
     * Key listener for hiding the keyboard when the "Enter" button is tapped.
     */
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}