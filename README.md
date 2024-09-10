Add it in your root build.gradle at the end of repositories:

```
repositories {
	mavenCentral()
	maven { url 'https://jitpack.io' }
}
```

Add the dependency

```

implementation 'com.github.M-Hammad1980:in-app-update:1.0.2'

```


in activity class 
```
private var appUpdateManagerLib: InAppUpdateManager? = null
private var updateLauncher: ActivityResultLauncher<IntentSenderRequest> ?= null
```

oncreate 
```
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)


        // Initialize the InAppUpdateManager
        appUpdateManagerLib = InAppUpdateManager(this)

        // Register the activity result launcher
        updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                // Handle update failure if needed
                showToast(getString(R.string.update_failed))
            }
        }

	//remote version code must greater then current version		 
        updateLauncher?.let {
            appUpdateManagerLib?.checkForUpdate(currentVersion = BuildConfig.VERSION_CODE,
                remoteVersion = 2,
                it, binding.root)
        }
    }
```

on stop
```
override fun onStop() {
        super.onStop()
        appUpdateManagerLib?.unregisterListener()
    }
```

on destroy
```
override fun onDestroy() {
        super.onDestroy()
        appUpdateManagerLib?.unregisterListener()
    }
```


onresume 
```
override fun onResume() {
        super.onResume()
        appUpdateManagerLib?.onResume()
    }
```
