import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import BottomNavbar from "~/components/BottomNavbar";
import { useLoaderData } from "@remix-run/react";
import { styled } from '@mui/material/styles';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import CardMedia from '@mui/material/CardMedia';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';
import Collapse from '@mui/material/Collapse';
import Avatar from '@mui/material/Avatar';
import IconButton, { IconButtonProps } from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { red } from '@mui/material/colors';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ShareIcon from '@mui/icons-material/Share';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import MoreVertIcon from '@mui/icons-material/MoreVert';
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
            <Card key={key} className={"my-4"} style={{minWidth: 500}}>
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
                  <div className="flex-col w-full" style={{textAlign: "right"}}>
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
