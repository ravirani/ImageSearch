package com.ravcode.imagesearch.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ravcode.imagesearch.R;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdvancedImageFiltersFragment.OnAdvancedImageFiltersSetListener} interface
 * to handle interaction events.
 * Use the {@link AdvancedImageFiltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AdvancedImageFiltersFragment extends DialogFragment {

    public static String[] SIZE_PICKER_VALUES = new String[] {"small", "medium", "large", "xlarge"};
    public static final String[] COLOR_PICKER_VALUES = new String[] {"black","blue","brown","gray","green","orange","pink","purple","red","teal","white","yellow"};
    public static final String[] TYPE_PICKER_VALUES = new String[] {"face","photo","clipart","lineart"};

    private static final String ARG_SIZE = "size";
    private static final String ARG_COLOR = "color";
    private static final String ARG_TYPE = "type";
    private static final String ARG_SITE = "site";

    private String mSize;
    private String mColor;
    private String mType;
    private String mSite;

    private Spinner sSize;
    private Spinner sColor;
    private Spinner sType;
    private EditText etSiteName;

    private OnAdvancedImageFiltersSetListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param size Image size option
     * @param color Image color option
     * @param type Image type option
     * @param siteName Image source
     * @return A new instance of fragment AdvancedImageFiltersFragment.
     */
    public static AdvancedImageFiltersFragment newInstance(String size, String color, String type, String siteName) {
        AdvancedImageFiltersFragment fragment = new AdvancedImageFiltersFragment();
        Bundle args = new Bundle();

        String defaultSize = size != null ? size : SIZE_PICKER_VALUES[0];
        String defaultColor = color != null ? color : COLOR_PICKER_VALUES[0];
        String defaultType = type != null ? type : TYPE_PICKER_VALUES[0];
        args.putString(ARG_SIZE, defaultSize);
        args.putString(ARG_COLOR, defaultColor);
        args.putString(ARG_TYPE, defaultType);
        args.putString(ARG_SITE, siteName);
        fragment.setArguments(args);
        return fragment;
    }
    public AdvancedImageFiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSize = getArguments().getString(ARG_SIZE);
            mColor = getArguments().getString(ARG_COLOR);
            mType = getArguments().getString(ARG_TYPE);
            mSite = getArguments().getString(ARG_SITE);
        }
    }

    private void setupPickers() {

        // Size Picker
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, SIZE_PICKER_VALUES);
        sSize.setAdapter(sizeAdapter);
        if (mSize != null) {
            sSize.setSelection(Arrays.asList(SIZE_PICKER_VALUES).indexOf(mSize));
        }

        // Color Picker
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, COLOR_PICKER_VALUES);
        sColor.setAdapter(colorAdapter);
        if (mColor != null) {
            sColor.setSelection(Arrays.asList(COLOR_PICKER_VALUES).indexOf(mColor));
        }

        // Type Picker
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TYPE_PICKER_VALUES);
        sType.setAdapter(typeAdapter);
        if (mType != null) {
            sType.setSelection(Arrays.asList(TYPE_PICKER_VALUES).indexOf(mType));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set the title of the fragment
        getDialog().setTitle("Advanced Filters");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_advanced_image_filters, container, false);

        // Get references to view
        sSize = (Spinner) rootView.findViewById(R.id.sSize);
        sColor = (Spinner) rootView.findViewById(R.id.sColor);
        sType = (Spinner) rootView.findViewById(R.id.sType);
        etSiteName = (EditText) rootView.findViewById(R.id.etSiteName);

        // Initialize view state
        setupPickers();
        if (mSite != null) {
            etSiteName.setText(mSite);
        }

        // Add onClickHandler
        Button bSaveButton = (Button) rootView.findViewById(R.id.bSaveFilter);
        bSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFiltersSet();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAdvancedImageFiltersSetListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAvancedImageFiltersSetListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onFiltersSet() {
        if (mListener != null) {
            String size = sSize.getSelectedItem().toString();
            String color = sColor.getSelectedItem().toString();
            String type = sType.getSelectedItem().toString();
            String siteName = etSiteName.getText().toString();
            mListener.OnAdvancedImageFiltersSet(size, color, type, siteName);
            dismiss();
        }
    }

    public interface OnAdvancedImageFiltersSetListener {
        public void OnAdvancedImageFiltersSet(String size, String color, String type, String siteName);
    }

}
