package com.maggie.rapidsync

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maggie.rapidsync.adapters.MealPlanAdapter
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.hide
import com.maggie.rapidsync.commons.show
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityMealPlanBinding
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.MealPlan
import com.maggie.rapidsync.model.pojo.StripeConfigResponse
import com.maggie.rapidsync.viewmodel.MealPlanViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MealPlanActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMealPlanBinding.inflate(layoutInflater)
    }

    private lateinit var paymentSheet: PaymentSheet


    private val viewModel: MealPlanViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)


        getUserPlans()

        binding.buttonReleaseSlot.setOnClickListener { _ ->
            viewModel.cancelMealPlan()
            booked = false
            binding.textViewBookedSlot.hide()
            binding.cardViewParkingSlot.hide()
        }

        lifecycleScope.launch {
            viewModel.stripeConfigResponse.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val stripeConfigResponse = result.body
                        stripeConfigResponse?.let {
                            createStripePaymentIntent(stripeConfigResponse)
                        }
                    }

                    is NetworkResult.Error -> {
                        showToast("Error: ${result.errorMessage}")
                    }

                    is NetworkResult.Loading -> {
                        // Show loading
                    }

                    else -> {
                    }
                }
            }

        }

    }


    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Timber.tag("Canceled").e("PaymentSheetResult.Canceled")
                showToast("Payment Canceled")
            }

            is PaymentSheetResult.Failed -> {
                Timber.tag("Canceled").e(paymentSheetResult.error.toString())
                paymentSheetResult.error.printStackTrace()
                showToast("Payment Failed")
            }

            is PaymentSheetResult.Completed -> {

                Timber.tag("Completed").e("PaymentSheetResult.Completed")
                showToast("Payment Successful")
                viewModel.addUserMealPlan(mealPlan)

            }
        }
    }


    private lateinit var mealPlan: MealPlan
    private fun fetchMealPlan() {
        viewModel.fetchMealPlans()
        lifecycleScope.launch {
            viewModel.mealPlans.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val mealPlans = result.body

                        val adapter = MealPlanAdapter(mealPlans!!, booked) { mealPlan ->
                            if (binding.textViewBookedSlot.isVisible) {
                                showToast("You already have a meal plan")
                            } else {
                                this@MealPlanActivity.mealPlan = mealPlan
                                viewModel.createStripePaymentIntent(mealPlan.price)
//                                viewModel.addUserMealPlan(mealPlan)

                            }
                        }
                        binding.recyclerViewSlots.layoutManager =
                            LinearLayoutManager(this@MealPlanActivity)
                        binding.recyclerViewSlots.adapter = adapter
                    }

                    is NetworkResult.Error -> {
                        showToast("Error: ${result.errorMessage}")
                    }

                    is NetworkResult.Loading -> {
                        // Show loading
//                        showToast("Loading")
                    }

                    else -> {
                    }
                }
            }
        }

    }


    private fun createStripePaymentIntent(
        createStripePaymentIntent: StripeConfigResponse,
    ) {

        val customerConfig = PaymentSheet.CustomerConfiguration(
            createStripePaymentIntent.customerId,
            createStripePaymentIntent.ephemeralKeySecret
        )
        PaymentConfiguration.init(
            this,
            Constants.STRIPE_PUBLISHABLE_KEY,
            Constants.STRIPE_ACCOUNT_ID
        )

        Stripe(
            this,
            PaymentConfiguration.getInstance(this).publishableKey,
            stripeAccountId = Constants.STRIPE_ACCOUNT_ID, enableLogging = true
        )

        Timber.tag("PaymentIntent").e(createStripePaymentIntent.toString())
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret = createStripePaymentIntent.clientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Rapid Sync",
                customer = customerConfig
            )
        )
    }

    private var booked = false

    @SuppressLint("SetTextI18n")
    private fun getUserPlans() {
        viewModel.fetchUserMealPlan()

        lifecycleScope.launch {
            viewModel.userMealPlan.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val mealPlan = result.body
                        if (mealPlan != null) {
                            booked = true
                            binding.textViewBookedSlot.show()
                            binding.cardViewParkingSlot.show()
                            binding.textViewLocation.text = mealPlan.mealPlan.name
                            binding.textViewSpeciality.text = mealPlan.mealPlan.description
                            binding.textViewId.text = "$${mealPlan.mealPlan.price}"
                        } else {
                            binding.textViewBookedSlot.hide()
                            binding.cardViewParkingSlot.hide()
                            booked = false
                        }
                        fetchMealPlan()
                    }

                    is NetworkResult.Error -> {
//                        showToast("Error: ${result.errorMessage}")
                        fetchMealPlan()
                    }

                    is NetworkResult.Loading -> {
//                        showToast("Loading")
                    }

                    else -> {
                    }
                }
            }
        }
    }

    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, MealPlanActivity::class.java)
        }
    }


}