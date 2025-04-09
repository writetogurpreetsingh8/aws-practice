import { Component } from '@angular/core';
import { LoaderService } from '../../loader.service.';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [NgIf],
  template: `
  <div class="loader-overlay" *ngIf="loader.isLoading()">
    <div class="spinner"></div>
  </div>`,
  styleUrl: './loader.component.css'
})
export class LoaderComponent {
  constructor(public loader: LoaderService) {}
}
