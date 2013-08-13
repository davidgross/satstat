package com.vonglasow.michael.lsrntools;

import java.util.Locale;


import android.content.Context;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
	private LocationManager mLocationManager;

	protected static TextView gpsLat;
	protected static TextView gpsLon;
	protected static TextView orDeclination;
	protected static TextView gpsSpeed;
	protected static TextView gpsAlt;
	protected static TextView gpsTime;
	protected static TextView gpsBearing;
	protected static TextView gpsAccuracy;
	protected static TextView gpsOrientation;
	/*
	private PowerManager pm;
	private WakeLock wl;
	*/


    /**
     * The {@link LocationListener} for updating the GPS display.
     */
	private final LocationListener mLocationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	      // Called when a new location is found by the location provider.
	    	if (location.hasAccuracy()) {
	    		gpsAccuracy.setText(String.format("%.0f", location.getAccuracy()));
	    	};
	    	gpsLat.setText(String.format("%.5f", location.getLatitude()));
	    	gpsLon.setText(String.format("%.5f", location.getLongitude()));
	    	gpsTime.setText(String.format("%1$tH:%1$tM:%1$tS", location.getTime()));
	    	if (location.hasAltitude()) {
	    		gpsAlt.setText(String.format("%.0f", location.getAltitude()));
	    		orDeclination.setText(String.format("%.0f", new GeomagneticField(
	    				(float) location.getLatitude(),
	    				(float) location.getLongitude(),
	    				(float) location.getAltitude(),
	    				location.getTime()
	    				).getDeclination()));
	    	}
	    	if (location.hasBearing()) {
	    		gpsBearing.setText(String.format("%.0f", location.getBearing()));
	    		gpsOrientation.setText(
	    				(location.getBearing() < 11.25) ? "N" :
	    					(location.getBearing() < 33.75) ? "NNE" :
	    						(location.getBearing() < 56.25) ? "NE" :
	    							(location.getBearing() < 78.75) ? "ENE" :
	    								(location.getBearing() < 101.25) ? "E" :
	    									(location.getBearing() < 123.75) ? "ESE" :
	    										(location.getBearing() < 146.25) ? "SE" :
	    											(location.getBearing() < 168.75) ? "SSE" :
	    												(location.getBearing() < 191.25) ? "S" :
	    													(location.getBearing() < 213.75) ? "SSW" :
	    														(location.getBearing() < 236.25) ? "SW" :
	    															(location.getBearing() < 258.75) ? "WSW" :
	    																(location.getBearing() < 280.25) ? "W" :
	    																	(location.getBearing() < 302.75) ? "WNW" :
	    																		(location.getBearing() < 325.25) ? "NW" :
	    																			(location.getBearing() < 347.75) ? "NNW" :
	    																				"N"
	    				);
	    	}
	    	if (location.hasSpeed()) {
	    		gpsSpeed.setText(String.format("%.0f", (location.getSpeed()) * 3.6));
	    	}
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
    	mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	
    	// SCREEN_BRIGHT_WAKE_LOCK is deprecated
    	/*
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Sensor Monitor");
        wl.acquire();
        */
    }
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    @Override
    protected void onStop() {
    	mLocationManager.removeUpdates(mLocationListener);
        super.onStop();
    }
    
    // we don't use wake locks
    /*
    @Override
    protected void onDestroy() {
    	wl.release();
        super.onDestroy();
    }
    */
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
        	Fragment fragment;
            switch (position) {
            case 0:
                fragment = new GpsSectionFragment();
                return fragment;
            case 1:
            case 2:
                fragment = new DummySectionFragment();
                Bundle args = new Bundle();
                args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
                fragment.setArguments(args);
                return fragment;
            }
        return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    /**
     * The fragment which displays GPS data.
     */
    public static class GpsSectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public GpsSectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_gps, container, false);
            
            // Initialize controls
        	gpsLat = (TextView) rootView.findViewById(R.id.gpsLat);
        	gpsLon = (TextView) rootView.findViewById(R.id.gpsLon);
        	orDeclination = (TextView) rootView.findViewById(R.id.orDeclination);
        	gpsSpeed = (TextView) rootView.findViewById(R.id.gpsSpeed);
        	gpsAlt = (TextView) rootView.findViewById(R.id.gpsAlt);
        	gpsTime = (TextView) rootView.findViewById(R.id.gpsTime);
        	gpsBearing = (TextView) rootView.findViewById(R.id.gpsBearing);
        	gpsAccuracy = (TextView) rootView.findViewById(R.id.gpsAccuracy);
        	gpsOrientation = (TextView) rootView.findViewById(R.id.gpsOrientation);

            return rootView;
        }
    }
}