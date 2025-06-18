import android.telecom.Call;

import com.example.kicklog.model.PlayerResponse;

public interface ApiService {
    @Headers({
            "x-apisports-key: c0e6fd2b26a5420890e93b4ed51ecb71"
    })
    @GET("players")
    Call<PlayerResponse> getPlayers(@Query("team") int teamId, @Query("season") int season);
}

