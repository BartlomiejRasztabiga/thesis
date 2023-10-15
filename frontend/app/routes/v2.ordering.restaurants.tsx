import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import BottomNavbar from "~/components/BottomNavbar";
import { useLoaderData } from "@remix-run/react";
import Card from "@mui/material/Card";
import CardMedia from "@mui/material/CardMedia";
import CardContent from "@mui/material/CardContent";
import { CardActionArea } from "@mui/material";

export async function loader({ request }: LoaderArgs) {
  const restaurants = await getRestaurants(request);
  return json({ restaurants });
}

export default function V2RestaurantsPage() {
  const data = useLoaderData<typeof loader>();

  return (
    <div className="flex flex-col h-full">
      <div className="h-full">
        <div className="flex flex-col w-80 mx-auto">
          {data.restaurants.map((restaurant, key) => (
            <Card key={key} className={"my-4"} style={{ minWidth: 500 }}>
              <CardActionArea>
                <CardMedia
                  component="img"
                  height="194"
                  image={restaurant.imageUrl}
                />
                <CardContent>
                  <div className="flex flex-row w-full">
                    <div className="flex-col w-full">
                      <h5 className="text-lg font-bold">{restaurant.name}</h5>
                      <p>delivery fee</p>
                    </div>
                    <div
                      className="flex-col w-full"
                      style={{ textAlign: "right" }}
                    >
                      <h5>&nbsp;</h5>
                      <p>rating</p>
                    </div>
                  </div>
                </CardContent>
              </CardActionArea>
            </Card>
          ))}
        </div>
      </div>
      <div className="justify-end">
        <BottomNavbar />
      </div>
    </div>
  );
}
