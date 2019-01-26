package com.ulicae.cinelog.data.services.wishlist;

import com.ulicae.cinelog.data.TmdbSerieRepository;
import com.ulicae.cinelog.data.WishlistSerieRepository;
import com.ulicae.cinelog.data.dao.DaoSession;
import com.ulicae.cinelog.data.dao.TmdbSerie;
import com.ulicae.cinelog.data.dao.WishlistSerie;
import com.ulicae.cinelog.data.dto.data.WishlistDataDto;
import com.ulicae.cinelog.data.dto.data.WishlistSerieToSerieDataDtoBuilder;

import java.util.ArrayList;
import java.util.List;

public class SerieWishlistService implements WishlistService {

    private final WishlistSerieRepository wishlistSerieRepository;
    private TmdbSerieRepository tmdbSerieRepository;
    private WishlistSerieToSerieDataDtoBuilder wishlistSerieToSerieDataDtoBuilder;

    public SerieWishlistService(DaoSession daoSession) {
        this(new WishlistSerieRepository(daoSession), new TmdbSerieRepository(daoSession), new WishlistSerieToSerieDataDtoBuilder());
    }

    SerieWishlistService(WishlistSerieRepository wishlistSerieRepository, TmdbSerieRepository tmdbSerieRepository, WishlistSerieToSerieDataDtoBuilder wishlistSerieToSerieDataDtoBuilder) {
        this.wishlistSerieRepository = wishlistSerieRepository;
        this.tmdbSerieRepository = tmdbSerieRepository;
        this.wishlistSerieToSerieDataDtoBuilder = wishlistSerieToSerieDataDtoBuilder;
    }

    public void createSerieData(WishlistDataDto wishlistDataDto) {
        TmdbSerie tmdbSerie = null;
        if (wishlistDataDto.getTmdbId() != null) {
            tmdbSerie = new TmdbSerie(
                    wishlistDataDto.getTmdbId() != null ? wishlistDataDto.getTmdbId().longValue() : null,
                    wishlistDataDto.getPosterPath(),
                    wishlistDataDto.getOverview(),
                    wishlistDataDto.getFirstYear(),
                    wishlistDataDto.getReleaseDate());
            tmdbSerieRepository.createOrUpdate(tmdbSerie);
        }

        WishlistSerie wishlistSerie = new WishlistSerie(
                wishlistDataDto.getId(),
                tmdbSerie,
                wishlistDataDto.getTitle(),
                null
        );
        wishlistSerieRepository.createOrUpdate(wishlistSerie);
    }

    public List<WishlistDataDto> getAll() {
        List<WishlistSerie> wishlistSeries = wishlistSerieRepository.findAll();

        List<WishlistDataDto> serieDataDtos = new ArrayList<>();
        for (WishlistSerie wishlistSerie : wishlistSeries) {
            serieDataDtos.add(wishlistSerieToSerieDataDtoBuilder.build(wishlistSerie));
        }
        return serieDataDtos;
    }

    public void delete(WishlistDataDto wishlistDataDto) {
        wishlistSerieRepository.delete(wishlistDataDto.getId());
    }
}