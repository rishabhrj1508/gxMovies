package com.endava.example.mapper;

import org.springframework.stereotype.Component;

import com.endava.example.dto.FavoriteDTO;
import com.endava.example.entity.Favorite;
import com.endava.example.entity.Movie;
import com.endava.example.entity.User;

@Component
public class FavoriteMapper {
	
	private MovieMapper movieMapper;

	public FavoriteMapper(MovieMapper movieMapper) {
		super();
		this.movieMapper = movieMapper;
	}

	public FavoriteDTO toDto(Favorite favorite) {
		FavoriteDTO dto = new FavoriteDTO();
		dto.setFavoriteId(favorite.getFavoriteId());
		dto.setUserId(favorite.getUser().getUserId());
		dto.setMovieId(favorite.getMovie().getMovieId());
		dto.setMovieDTO(movieMapper.toDto(favorite.getMovie()));
		return dto;
	}

	public Favorite toEntity(User user, Movie movie) {
		Favorite favorite = new Favorite();
		favorite.setUser(user);
		favorite.setMovie(movie);
		return favorite;
	}

}
