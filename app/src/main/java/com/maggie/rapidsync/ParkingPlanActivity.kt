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
import com.maggie.rapidsync.adapters.ParkingPlanAdapter
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.hide
import com.maggie.rapidsync.commons.show
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityParkingPlanBinding
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.ParkingPlan
import com.maggie.rapidsync.model.pojo.StripeConfigResponse
import com.maggie.rapidsync.viewmodel.ParkingPlanViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ParkingPlanActivity : AppCompatActivity() {

    private val binding: ActivityParkingPlanBinding by lazy {
        ActivityParkingPlanBinding.inflate(layoutInflater)
    }
    private val viewModel: ParkingPlanViewModel by viewModels()

    private lateinit var paymentSheet: PaymentSheet


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getUserPlans()

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        binding.buttonReleaseSlot.setOnClickListener { _ ->
            viewModel.cancelParkingPlan()
            booked = false
            binding.textViewBookedSlot.hide()
            binding.cardViewParkingSlot.hide()
            Log.d("ParkingPlanActivity", "getUserPlans: Cancel plan ")
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
                viewModel.addUserParkingPlan(parkingPlan)

            }
        }
    }

    private lateinit var parkingPlan: ParkingPlan

    private fun getAllPlans() {
        viewModel.fetchParkingPlans()

        lifecycleScope.launch {
            viewModel.parkingPlans.collect {
                when (it) {
                    is NetworkResult.Loading -> {
//                        showToast("Loading")
                    }

                    is NetworkResult.Success -> {
                        binding.recyclerViewSlots.layoutManager =
                            LinearLayoutManager(this@ParkingPlanActivity)
                        binding.recyclerViewSlots.adapter = it.body?.let { slots ->
                            ParkingPlanAdapter(slots, booked) { parkingSlot ->
                                if (binding.textViewBookedSlot.isVisible) {
                                    showToast("You already have a parking plan")
                                } else {
                                    this@ParkingPlanActivity.parkingPlan = parkingSlot
                                    viewModel.createStripePaymentIntent(parkingSlot.price)
//                                    viewModel.addUserParkingPlan(parkingSlot)
                                }
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        showToast(it.errorMessage)
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
        viewModel.fetchUserParkingPlan()

        lifecycleScope.launch {
            viewModel.userParkingPlan.collect {
                when (it) {
                    is NetworkResult.Loading -> {
//                        showToast("Loading")
                    }

                    is NetworkResult.Success -> {
                        booked = it.body?.parkingPlan != null
                        getAllPlans()

                        binding.textViewBookedSlot.show()
                        binding.cardViewParkingSlot.show()
                        binding.textViewLocation.text = it.body?.parkingPlan?.name
                        binding.textViewSpeciality.text = it.body?.parkingPlan?.description
                        binding.textViewId.text = "$${it.body?.parkingPlan?.price}"
                    }

                    is NetworkResult.Error -> {
//                        showToast(it.errorMessage)
                        getAllPlans()
                    }

                    else -> {}
                }
            }
        }
    }

    companion object {
        fun newIntent(activity: AppCompatActivity) =
            Intent(activity, ParkingPlanActivity::class.java)
    }

}