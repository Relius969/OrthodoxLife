package org.telegram.pravzhizn.ui.churches.mode;

import org.telegram.pravzhizn.pravzhizn.CityObject;

/**
 * Created by vlad on 6/6/16.
 */
public interface ChurchQuery {

    interface Visitor {
        void visit(ByLocation byLocation);
        void visit(ByName byName);
        void visit(ByCity byCity);
        void visit(MyTemples myTemples);
    }

    void accept ( Visitor v );

    class ByLocation implements ChurchQuery {
        public final Double mLat;
        public final Double mLng;

        public ByLocation(Double lat, Double lng) {
            mLat = lat;
            mLng = lng;
        }

        @Override
        public void accept(final Visitor v) {
            v.visit(this);
        }
    }

    class ByName implements ChurchQuery {
        public final String mName;

        public ByName(String name) {
            mName = name;
        }

        @Override
        public void accept(final Visitor v) {
            v.visit(this);
        }
    }

    class MyTemples implements ChurchQuery {

        @Override
        public void accept(final Visitor v) {
            v.visit(this);
        }
    }

    class ByCity implements ChurchQuery {

        public final CityObject mCity;

        public ByCity(CityObject cityObject) {
            mCity = cityObject;
        }

        @Override
        public void accept(final Visitor v) {
            v.visit(this);
        }
    }
}
