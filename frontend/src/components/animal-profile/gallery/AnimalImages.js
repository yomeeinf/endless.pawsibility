import React from "react";
import CategoryTitle from "../../block/CategoryTitle";
import ImageInput from "../../input/image/gallery/ImageInput";
import { ContextMenuTrigger } from "react-contextmenu";
import { AdminGalleryContextMenu } from '../../context-menu/ContextMenus'
import { ANIMAL_GALLERY_ROUTE } from '../../../api/Api';
import '../../context-menu/contextmenu.css';
import "./animalimages.css";

class AnimalImages extends React.Component {
  constructor(props) {
    super(props);
    this.state = { selectedImage: "" }
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(props) {
    switch (props) {
      case "set": { this.props.setGalleryImageAsAvatar(this.state.selectedImage); break; }
      case "delete": { this.props.deleteGalleryImage(this.state.selectedImage); break; }
      default: { break; }
    }
  }

  render() {
    return (
      <div className="animalImages" >
        <div className="animalImages-category">
          <CategoryTitle title="Images" />
        </div>
        <div className="animalImages-gallery">
          <ImageInput name={this.props.name} handleChange={this.props.handleChange} />
          {this.props.animalGallery.map((image, i) => (
            <ContextMenuTrigger id="same_unique_identifier">
              <div className="animalImages-item-border" key={i} onContextMenu={() => this.setState({ selectedImage: image.id })}>
                <div
                  className="animalImages-item"
                  style={{
                    backgroundImage: "url(" + ANIMAL_GALLERY_ROUTE + image.url + ")",
                    backgroundSize: "cover",
                  }}
                >
                </div>
              </div>
            </ContextMenuTrigger>
          ))}
        </div>
        <AdminGalleryContextMenu handleClick={this.handleClick} />
      </div >
    );
  };
}

export default AnimalImages;
